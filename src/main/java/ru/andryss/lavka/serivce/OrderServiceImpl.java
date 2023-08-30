package ru.andryss.lavka.serivce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andryss.lavka.exception.BadRequestException;
import ru.andryss.lavka.exception.NotFoundException;
import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.entity.OrderEntity;
import ru.andryss.lavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrder;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrderResponse;
import ru.andryss.lavka.objects.utils.mapper.OrderMapper;
import ru.andryss.lavka.repository.CourierRepository;
import ru.andryss.lavka.repository.GroupOrderRepository;
import ru.andryss.lavka.repository.OffsetLimitPageable;
import ru.andryss.lavka.repository.OrderRepository;
import ru.andryss.lavka.objects.dto.OrderDto;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderRequest;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderResponse;
import ru.andryss.lavka.objects.mapping.get.order.GetOrderResponse;
import ru.andryss.lavka.objects.mapping.get.order.GetOrdersResponse;
import ru.andryss.lavka.serivce.assignment.OrderAssignService;
import ru.andryss.lavka.serivce.assignment.OrderAssignService.AssignedOrdersInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.andryss.lavka.objects.utils.IntervalEntityUtils.hasIntersections;
import static ru.andryss.lavka.objects.utils.IntervalEntityUtils.isInsideAnyInterval;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final GroupOrderRepository groupOrderRepository;

    private final OrderMapper orderMapper;

    private final OrderAssignService orderAssignService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, CourierRepository courierRepository, GroupOrderRepository groupOrderRepository, OrderMapper orderMapper, OrderAssignService orderAssignService) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.groupOrderRepository = groupOrderRepository;
        this.orderMapper = orderMapper;
        this.orderAssignService = orderAssignService;
    }

    @Override
    @Transactional
    public CreateOrderResponse addOrders(CreateOrderRequest request) {
        List<OrderEntity> orderEntitiesToSave = request.getOrders().stream()
                .map(orderMapper::mapOrderEntity)
                .peek(this::courierEntityHasNoIntersectingIntervals)
                .toList();
        List<OrderDto> response = orderRepository.saveAll(orderEntitiesToSave).stream()
                .map(orderMapper::mapOrderDto)
                .toList();
        return new CreateOrderResponse(response);
    }

    private void courierEntityHasNoIntersectingIntervals(OrderEntity orderEntity) {
        if (hasIntersections(orderEntity.getDeliveryHours()))
            throw new BadRequestException(String.format("delivery hours has intersections: %s", orderEntity.getDeliveryHours()));
    }

    @Override
    @Transactional(readOnly = true)
    public GetOrderResponse getOrderById(Long orderId) {
        Optional<OrderDto> orderDtoOptional = orderRepository.findById(orderId).map(orderMapper::mapOrderDto);
        if (orderDtoOptional.isEmpty())
            throw new NotFoundException(String.format("no order with id %d", orderId));
        return new GetOrderResponse(orderDtoOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public GetOrdersResponse getOrderRange(Integer offset, Integer limit) {
        Page<OrderEntity> orderEntityPage = orderRepository.findAll(OffsetLimitPageable.of(offset, limit));
        List<OrderDto> response = orderEntityPage.stream()
                .map(orderMapper::mapOrderDto)
                .toList();
        return new GetOrdersResponse(response, limit, offset);
    }

    @Override
    @Transactional
    public CompleteOrderResponse completeOrders(CompleteOrderRequestDto completeOrderRequestDto) {
        List<CompleteOrder> completeInfo = completeOrderRequestDto.getCompleteInfo();

        // Check if no order has completed more than 1 time
        Set<Long> completeOrderIds = new HashSet<>(completeInfo.size());
        Map<Long, Set<Long>> courierIdToCompleteOrderIds = new HashMap<>(completeInfo.size());

        completeInfo.forEach(completeOrder -> {
            if (completeOrderIds.contains(completeOrder.getOrderId()))
                throw new BadRequestException(String.format("can't complete order with id %d twice", completeOrder.getOrderId()));
            completeOrderIds.add(completeOrder.getOrderId());

            courierIdToCompleteOrderIds.computeIfAbsent(completeOrder.getCourierId(), id -> new HashSet<>())
                    .add(completeOrder.getOrderId());
        });

        // Check if all couriers exist
        Set<Long> completeCourierIds = courierIdToCompleteOrderIds.keySet();
        List<CourierEntity> fetchedCourierEntities = courierRepository.findAllByCourierIdIn(completeCourierIds);
        if (fetchedCourierEntities.size() != completeCourierIds.size())
            throw new BadRequestException(String.format("some couriers with ids from %s doesn't exist", completeCourierIds));

        // Check if all orders exist
        List<OrderEntity> fetchedOrderEntities = orderRepository.findAllByOrderIdIn(completeOrderIds);
        if (fetchedOrderEntities.size() != completeOrderIds.size())
            throw new BadRequestException(String.format("some orders with ids from %s doesn't exist", completeOrderIds));
        // Check if all orders can be completed
        if (fetchedOrderEntities.stream().anyMatch(orderEntity -> orderEntity.getAssignedGroupOrder() == null))
            throw new BadRequestException(String.format("some orders with ids from %s already assigned to some group", completeCourierIds));

        // Check if all orders was completed by assigned couriers
        fetchedCourierEntities.forEach(courierEntity -> {
            Set<Long> assignedOrderIds = courierEntity.getAssignedGroupOrders().stream()
                    .flatMap(groupOrders -> groupOrders.getOrders().stream())
                    .map(OrderEntity::getOrderId)
                    .collect(Collectors.toSet());
            Set<Long> completedOrderIds = courierIdToCompleteOrderIds.get(courierEntity.getCourierId());
            if (!assignedOrderIds.containsAll(completedOrderIds)) {
                completedOrderIds.removeAll(assignedOrderIds);
                throw new BadRequestException(String.format("courier with id %d can't complete %s: not assigned to him", courierEntity.getCourierId(), completedOrderIds));
            }
        });

        // Create mappings (courierId/orderId to CompletedTime)
        Map<Long, LocalDateTime> orderIdToCompletedTime = new HashMap<>(completeInfo.size());
        Map<Long, LocalDateTime> courierIdToCompletedTime = new HashMap<>(completeInfo.size());
        completeInfo.forEach(completeOrder -> {
            orderIdToCompletedTime.put(completeOrder.getOrderId(), completeOrder.getCompleteTime());
            courierIdToCompletedTime.put(completeOrder.getCourierId(), completeOrder.getCompleteTime());
        });

        // Check if all orders can be completed (again)
        if (fetchedOrderEntities.stream().anyMatch(orderEntity -> orderEntity.getCompletedTime() != null &&
                !orderEntity.getCompletedTime().equals(orderIdToCompletedTime.get(orderEntity.getOrderId()))))
            throw new BadRequestException(String.format("some orders with ids from %s has already completed at another time", completeOrderIds));

        // Check if all orders/couriers has right deliveryTime/workingHours
        fetchedOrderEntities.forEach(fetchedOrderEntity -> {
            LocalDateTime completedTime = orderIdToCompletedTime.get(fetchedOrderEntity.getOrderId());
            if (!fetchedOrderEntity.getAssignedGroupOrder().getAssignedDate().equals(completedTime.toLocalDate()))
                throw new BadRequestException(String.format("order with id %d belongs to a group which assigned another date, not %s",
                        fetchedOrderEntity.getOrderId(), completedTime.toLocalDate()));
            if (!isInsideAnyInterval(completedTime.toLocalTime(), fetchedOrderEntity.getDeliveryHours()))
                throw new BadRequestException(String.format("order with id %d can't be completed at %s: has different delivery hours",
                        fetchedOrderEntity.getOrderId(), completedTime.toLocalTime()));
        });
        fetchedCourierEntities.forEach(fetchedCourierEntity -> {
            LocalDateTime completedTime = courierIdToCompletedTime.get(fetchedCourierEntity.getCourierId());
            if (!isInsideAnyInterval(completedTime.toLocalTime(), fetchedCourierEntity.getWorkingHours()))
                throw new BadRequestException(String.format("courier with id %d can't complete at %s: has different working hours",
                        fetchedCourierEntity.getCourierId(), completedTime.toLocalTime()));
        });

        // Complete orders
        List<OrderDto> response = fetchedOrderEntities.stream()
                .peek(orderEntity -> orderEntity.setCompletedTime(orderIdToCompletedTime.get(orderEntity.getOrderId())))
                .map(orderMapper::mapOrderDto)
                .toList();
        return new CompleteOrderResponse(response);
    }

    @Override
    public OrderAssignResponse assignOrders(LocalDate date) {
        if (groupOrderRepository.existsByAssignedDateEquals(date))
            throw new BadRequestException(String.format("orders has already assigned at %s", date));

        AssignedOrdersInfo assignedOrdersInfo = orderAssignService.assignOrders(date);
        return new OrderAssignResponse(
                date.toString(),
                assignedOrdersInfo.getAssignedCouriersGroupOrders()
        );
    }
}
