package ru.yandex.yandexlavka.serivces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.entities.*;
import ru.yandex.yandexlavka.entities.exceptions.BadRequestException;
import ru.yandex.yandexlavka.entities.orders.CompleteOrder;
import ru.yandex.yandexlavka.entities.orders.CreateOrderDto;
import ru.yandex.yandexlavka.entities.orders.CreateOrderRequest;
import ru.yandex.yandexlavka.entities.orders.OrderDto;
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
        List<CompleteOrder> completeInfo = completeOrderRequestDto.getCompleteInfo();
        if (!orderRepository.isAllOrdersCorrect(completeInfo))
            throw new BadRequestException();
        return orderRepository.setAllOrdersCompletedTime(completeInfo);
    }
}
