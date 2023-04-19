package ru.yandex.yandexlavka.serivce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public List<OrderDto> addOrders(CreateOrderRequest request) {
        List<OrderEntity> orderEntitiesToSave = request.getOrders().stream()
                .map(orderMapper::mapOrderEntity)
                .toList();
        return orderRepository.saveAll(orderEntitiesToSave).stream()
                .map(orderMapper::mapOrderDto)
                .toList();
    }

    public Optional<OrderDto> getOrderById(Long orderId) {
        return orderRepository.findById(orderId).map(orderMapper::mapOrderDto);
    }

    public List<OrderDto> getOrderRange(Integer offset, Integer limit) {
        Page<OrderEntity> orderEntityPage = orderRepository.findAll(OffsetLimitPageable.of(offset, limit));
        return orderEntityPage.stream()
                .map(orderMapper::mapOrderDto)
                .toList();
    }

    public List<OrderDto> completeOrders(CompleteOrderRequestDto completeOrderRequestDto) {
        List<CompleteOrder> completeInfo = completeOrderRequestDto.getCompleteInfo();

        // Check if no order has completed more than 1 time
        List<Long> orderIdList = completeInfo.stream().map(CompleteOrder::getOrderId).distinct().toList();
        if (orderIdList.size() != completeInfo.size())
            throw BadRequestException.EMPTY;

        // Check if all couriers exist
        List<Long> courierIdList = completeInfo.stream().map(CompleteOrder::getCourierId).distinct().toList();
        List<CourierEntity> fetchedCourierEntities = courierRepository.findAllByCourierIdIn(courierIdList);
        if (fetchedCourierEntities.size() != courierIdList.size())
            throw BadRequestException.EMPTY;

        // Check if all orders exist
        List<OrderEntity> fetchedOrderEntities = orderRepository.findAllByOrderIdIn(orderIdList);
        if (fetchedOrderEntities.size() != orderIdList.size())
            throw BadRequestException.EMPTY;
        // Check if all orders can be completed
        if (fetchedOrderEntities.stream().anyMatch(orderEntity -> orderEntity.getAssignedCourierId() == null || orderEntity.getCompletedTime() != null))
            throw BadRequestException.EMPTY;

        // Complete orders
        Map<Long, LocalDateTime> orderIdToCompletedTime = new HashMap<>(completeInfo.size());
        completeInfo.forEach(completeOrder -> orderIdToCompletedTime.put(completeOrder.getOrderId(), completeOrder.getCompleteTime()));

        return fetchedOrderEntities.stream()
                .peek(orderEntity -> orderEntity.setCompletedTime(orderIdToCompletedTime.get(orderEntity.getOrderId())))
                .map(orderMapper::mapOrderDto)
                .toList();
    }
}
