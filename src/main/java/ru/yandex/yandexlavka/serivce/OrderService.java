package ru.yandex.yandexlavka.serivce;

import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<OrderDto> addOrders(CreateOrderRequest request);

    Optional<OrderDto> getOrderById(Long orderId);

    List<OrderDto> getOrderRange(Integer offset, Integer limit);

    List<OrderDto> completeOrders(CompleteOrderRequestDto completeOrderRequestDto);
}
