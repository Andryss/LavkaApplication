package ru.yandex.yandexlavka.repositories;

import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entities.dto.CourierDto;
import ru.yandex.yandexlavka.entities.dto.CreateCourierDto;
import ru.yandex.yandexlavka.entities.dto.CreateOrderDto;
import ru.yandex.yandexlavka.entities.dto.OrderDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    // FIXME: change db
    private long orderId = 0;
    private final List<OrderDto> orderDtoList = new ArrayList<>(1024);

    public OrderDto addOrder(CreateOrderDto createOrderDto) {
        OrderDto orderDto = new OrderDto(createOrderDto);
        orderDto.setOrderId(orderId++);
        orderDtoList.add(orderDto);
        return orderDto;
    }

    public List<OrderDto> addAllOrders(List<CreateOrderDto> createOrderDtoList) {
        List<OrderDto> orderDtos = new ArrayList<>(createOrderDtoList.size());
        createOrderDtoList.forEach(createOrderDto -> orderDtos.add(addOrder(createOrderDto)));
        return orderDtos;
    }

    public Optional<OrderDto> getOrderById(Long orderId) {
        return orderDtoList.stream().filter(orderDto -> orderDto.getOrderId().equals(orderId)).findAny();
    }

    public List<OrderDto> getOrderRange(int offset, int limit) {
        if (offset >= orderDtoList.size()) return Collections.emptyList();
        return orderDtoList.subList(offset, Math.min(offset + limit, orderDtoList.size()));
    }

}
