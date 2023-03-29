package ru.yandex.yandexlavka.repositories;

import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entities.CompleteOrder;
import ru.yandex.yandexlavka.entities.dto.CourierDto;
import ru.yandex.yandexlavka.entities.dto.CreateCourierDto;
import ru.yandex.yandexlavka.entities.dto.CreateOrderDto;
import ru.yandex.yandexlavka.entities.dto.OrderDto;

import java.util.*;

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

    public Optional<OrderDto> setOrderCompletedTime(Long orderId, Date completedTime) {
        return getOrderById(orderId).map(orderDto -> {
            orderDto.setCompletedTime(completedTime);
            return orderDto;
        });
    }

    public List<OrderDto> setAllOrdersCompletedTime(List<CompleteOrder> completeOrderList) {
        List<OrderDto> setOrders = new ArrayList<>(completeOrderList.size());
        completeOrderList.forEach(completeOrder -> {
            Optional<OrderDto> orderDto = setOrderCompletedTime(
                    completeOrder.getOrderId(),
                    completeOrder.getCompleteTime()
            );
            orderDto.ifPresent(setOrders::add);
        });
        return setOrders;
    }

}
