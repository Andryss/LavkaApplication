package ru.yandex.yandexlavka.serivce;

import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.get.order.GetOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.get.order.GetOrdersResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    CreateOrderResponse addOrders(CreateOrderRequest request);

    GetOrderResponse getOrderById(Long orderId);

    GetOrdersResponse getOrderRange(Integer offset, Integer limit);

    CompleteOrderResponse completeOrders(CompleteOrderRequestDto completeOrderRequestDto);

    OrderAssignResponse assignOrders(LocalDate date);
}
