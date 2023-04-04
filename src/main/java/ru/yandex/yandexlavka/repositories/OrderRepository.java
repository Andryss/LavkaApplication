package ru.yandex.yandexlavka.repositories;

import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entities.orders.CompleteOrder;
import ru.yandex.yandexlavka.entities.orders.OrderDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    // FIXME: change db
    private long orderId = 0;
    private final List<OrderDto> orderDtoList = new ArrayList<>(1024);
    private final List<Long> assignedCourierIdList = new ArrayList<>(1024);

    public OrderDto addOrder(OrderDto orderDto) {
        orderDto.setOrderId(orderId++);
        orderDtoList.add(orderDto);
        assignedCourierIdList.add(null);
        return orderDto;
    }

    public List<OrderDto> addAllOrders(List<OrderDto> orderDtoList) {
        List<OrderDto> orderDtos = new ArrayList<>(orderDtoList.size());
        orderDtoList.forEach(orderDto -> orderDtos.add(addOrder(orderDto)));
        return orderDtos;
    }

    public Optional<OrderDto> getOrderById(Long orderId) {
        return orderDtoList.stream().filter(orderDto -> orderDto.getOrderId().equals(orderId)).findAny();
    }

    public List<OrderDto> getOrderRange(int offset, int limit) {
        if (offset >= orderDtoList.size()) return Collections.emptyList();
        return orderDtoList.subList(offset, Math.min(offset + limit, orderDtoList.size()));
    }

    private boolean isOrderCorrect(CompleteOrder completeOrder) {
        Optional<OrderDto> orderById = getOrderById(completeOrder.getOrderId());
        if (orderById.isEmpty()) return false;
        int index = orderDtoList.indexOf(orderById.get());
        Long courierId = assignedCourierIdList.get(index);
        return completeOrder.getCourierId().equals(courierId);
    }

    public boolean isAllOrdersCorrect(List<CompleteOrder> completeOrderList) {
        return completeOrderList.stream().allMatch(this::isOrderCorrect);
    }

    private void completeOrder(CompleteOrder completeOrder) {
        getOrderById(completeOrder.getOrderId())
                .ifPresent(orderDto -> orderDto.setCompletedTime(completeOrder.getCompleteTime()));
    }

    public List<OrderDto> setAllOrdersCompletedTime(List<CompleteOrder> completeOrderList) {
        List<OrderDto> setOrders = new ArrayList<>(completeOrderList.size());
        completeOrderList.forEach(completedOrder -> {
            completeOrder(completedOrder);
            Optional<OrderDto> orderById = getOrderById(completedOrder.getOrderId());
            orderById.ifPresent(setOrders::add);
        });
        return setOrders;
    }

}
