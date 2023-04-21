package ru.yandex.yandexlavka.serivce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.exception.BadRequestException;
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;
import ru.yandex.yandexlavka.objects.mapper.OrderMapper;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrder;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.repository.CourierRepository;
import ru.yandex.yandexlavka.repository.OffsetLimitPageable;
import ru.yandex.yandexlavka.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, CourierRepository courierRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public List<OrderDto> addOrders(CreateOrderRequest request) {
        List<OrderEntity> orderEntitiesToSave = request.getOrders().stream()
                .map(orderMapper::mapOrderEntity)
                .toList();
        return orderRepository.saveAll(orderEntitiesToSave).stream()
                .map(orderMapper::mapOrderDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(Long orderId) {
        return orderRepository.findById(orderId).map(orderMapper::mapOrderDto);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrderRange(Integer offset, Integer limit) {
        Page<OrderEntity> orderEntityPage = orderRepository.findAll(OffsetLimitPageable.of(offset, limit));
        return orderEntityPage.stream()
                .map(orderMapper::mapOrderDto)
                .toList();
    }

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

        // Complete orders
        Map<Long, LocalDateTime> orderIdToCompletedTime = new HashMap<>(completeInfo.size());
        completeInfo.forEach(completeOrder -> orderIdToCompletedTime.put(completeOrder.getOrderId(), completeOrder.getCompleteTime()));

        return fetchedOrderEntities.stream()
                .peek(orderEntity -> orderEntity.setCompletedTime(orderIdToCompletedTime.get(orderEntity.getOrderId())))
                .map(orderMapper::mapOrderDto)
                .toList();
    }
}
