package ru.yandex.yandexlavka.serivce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.exception.BadRequestException;
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrder;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.objects.utils.mapper.OrderMapper;
import ru.yandex.yandexlavka.repository.CourierRepository;
import ru.yandex.yandexlavka.repository.GroupOrderRepository;
import ru.yandex.yandexlavka.repository.OffsetLimitPageable;
import ru.yandex.yandexlavka.repository.OrderRepository;
import ru.yandex.yandexlavka.serivce.assignment.OrderAssignService;
import ru.yandex.yandexlavka.serivce.assignment.OrderAssignService.AssignedOrdersInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.yandexlavka.objects.utils.IntervalEntityUtils.hasIntersections;
import static ru.yandex.yandexlavka.objects.utils.IntervalEntityUtils.isInsideAnyInterval;

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
    public List<OrderDto> addOrders(CreateOrderRequest request) {
        List<OrderEntity> orderEntitiesToSave = request.getOrders().stream()
                .map(orderMapper::mapOrderEntity)
                .peek(this::courierEntityHasNoIntersectingIntervals)
                .toList();
        return orderRepository.saveAll(orderEntitiesToSave).stream()
                .map(orderMapper::mapOrderDto)
                .toList();
    }

    private void courierEntityHasNoIntersectingIntervals(OrderEntity orderEntity) {
        if (hasIntersections(orderEntity.getDeliveryHours()))
            throw BadRequestException.EMPTY;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(Long orderId) {
        return orderRepository.findById(orderId).map(orderMapper::mapOrderDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrderRange(Integer offset, Integer limit) {
        Page<OrderEntity> orderEntityPage = orderRepository.findAll(OffsetLimitPageable.of(offset, limit));
        return orderEntityPage.stream()
                .map(orderMapper::mapOrderDto)
                .toList();
    }

    @Override
    @Transactional
    public List<OrderDto> completeOrders(CompleteOrderRequestDto completeOrderRequestDto) {
        List<CompleteOrder> completeInfo = completeOrderRequestDto.getCompleteInfo();

        // Check if no order has completed more than 1 time
        Set<Long> completeOrderIds = new HashSet<>(completeInfo.size());
        Map<Long, Set<Long>> courierIdToCompleteOrderIds = new HashMap<>(completeInfo.size());

        completeInfo.forEach(completeOrder -> {
            if (completeOrderIds.contains(completeOrder.getOrderId()))
                throw BadRequestException.EMPTY;
            completeOrderIds.add(completeOrder.getOrderId());

            courierIdToCompleteOrderIds.computeIfAbsent(completeOrder.getCourierId(), id -> new HashSet<>())
                    .add(completeOrder.getOrderId());
        });

        // Check if all couriers exist
        Set<Long> completeCourierIds = courierIdToCompleteOrderIds.keySet();
        List<CourierEntity> fetchedCourierEntities = courierRepository.findAllByCourierIdIn(completeCourierIds);
        if (fetchedCourierEntities.size() != completeCourierIds.size())
            throw BadRequestException.EMPTY;

        // Check if all orders exist
        List<OrderEntity> fetchedOrderEntities = orderRepository.findAllByOrderIdIn(completeOrderIds);
        if (fetchedOrderEntities.size() != completeOrderIds.size())
            throw BadRequestException.EMPTY;
        // Check if all orders can be completed
        if (fetchedOrderEntities.stream().anyMatch(orderEntity -> orderEntity.getAssignedGroupOrder() == null || orderEntity.getCompletedTime() != null))
            throw BadRequestException.EMPTY;

        // Check if all orders was completed by assigned couriers
        fetchedCourierEntities.forEach(courierEntity -> {
            Set<Long> assignedOrderIds = courierEntity.getAssignedGroupOrders().stream()
                    .flatMap(groupOrders -> groupOrders.getOrders().stream())
                    .map(OrderEntity::getOrderId)
                    .collect(Collectors.toSet());
            Set<Long> completedOrderIds = courierIdToCompleteOrderIds.get(courierEntity.getCourierId());
            if (!assignedOrderIds.containsAll(completedOrderIds))
                throw BadRequestException.EMPTY;
        });

        // Check if all orders/couriers has right deliveryTime/workingHours
        Map<Long, LocalDateTime> orderIdToCompletedTime = new HashMap<>(completeInfo.size());
        Map<Long, LocalDateTime> courierIdToCompletedTime = new HashMap<>(completeInfo.size());
        completeInfo.forEach(completeOrder -> {
            orderIdToCompletedTime.put(completeOrder.getOrderId(), completeOrder.getCompleteTime());
            courierIdToCompletedTime.put(completeOrder.getCourierId(), completeOrder.getCompleteTime());
        });

        fetchedOrderEntities.forEach(fetchedOrderEntity -> {
            LocalDateTime completedTime = orderIdToCompletedTime.get(fetchedOrderEntity.getOrderId());
            if (!isInsideAnyInterval(completedTime.toLocalTime(), fetchedOrderEntity.getDeliveryHours()))
                throw BadRequestException.EMPTY;
        });
        fetchedCourierEntities.forEach(fetchedCourierEntity -> {
            LocalDateTime completedTime = courierIdToCompletedTime.get(fetchedCourierEntity.getCourierId());
            if (!isInsideAnyInterval(completedTime.toLocalTime(), fetchedCourierEntity.getWorkingHours()))
                throw BadRequestException.EMPTY;
        });

        // Complete orders
        return fetchedOrderEntities.stream()
                .peek(orderEntity -> orderEntity.setCompletedTime(orderIdToCompletedTime.get(orderEntity.getOrderId())))
                .map(orderMapper::mapOrderDto)
                .toList();
    }

    @Override
    public List<OrderAssignResponse> assignOrders(LocalDate date) {
        if (groupOrderRepository.existsByAssignedDateEquals(date))
            throw BadRequestException.EMPTY;

        AssignedOrdersInfo assignedOrdersInfo = orderAssignService.assignOrders(date);
        OrderAssignResponse response = new OrderAssignResponse(
                date.toString(),
                assignedOrdersInfo.getAssignedCouriersGroupOrders()
        );
        return Collections.singletonList(response);
    }
}
