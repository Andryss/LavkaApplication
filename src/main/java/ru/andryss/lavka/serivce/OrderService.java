package ru.andryss.lavka.serivce;

import ru.andryss.lavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrderResponse;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderRequest;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderResponse;
import ru.andryss.lavka.objects.mapping.get.order.GetOrderResponse;
import ru.andryss.lavka.objects.mapping.get.order.GetOrdersResponse;

import java.time.LocalDate;

public interface OrderService {

    CreateOrderResponse addOrders(CreateOrderRequest request);

    GetOrderResponse getOrderById(Long orderId);

    GetOrdersResponse getOrderRange(Integer offset, Integer limit);

    CompleteOrderResponse completeOrders(CompleteOrderRequestDto completeOrderRequestDto);

    OrderAssignResponse assignOrders(LocalDate date);
}
