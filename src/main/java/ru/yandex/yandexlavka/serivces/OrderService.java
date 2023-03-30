package ru.yandex.yandexlavka.serivces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.entities.*;
import ru.yandex.yandexlavka.entities.dto.*;
import ru.yandex.yandexlavka.repositories.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderDto> addOrders(CreateOrderRequest request) {
        List<CreateOrderDto> createOrderDtoList = request.getOrders();
        return orderRepository.addAllOrders(createOrderDtoList);
    }

    public Optional<OrderDto> getOrderById(Long orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public List<OrderDto> getOrderRange(Integer offset, Integer limit) {
        return orderRepository.getOrderRange(offset, limit);
    }

    public List<OrderDto> completeOrders(CompleteOrderRequestDto completeOrderRequestDto) {
        // TODO: add check
        List<CompleteOrder> completeInfo = completeOrderRequestDto.getCompleteInfo();
        return orderRepository.setAllOrdersCompletedTime(completeInfo);
    }
}
