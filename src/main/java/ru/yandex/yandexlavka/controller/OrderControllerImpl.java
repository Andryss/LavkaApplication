package ru.yandex.yandexlavka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.yandexlavka.controller.validator.CreateOrderRequestValidator;
import ru.yandex.yandexlavka.exception.BadRequestException;
import ru.yandex.yandexlavka.exception.NotFoundException;
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.get.order.GetOrderResponse;
import ru.yandex.yandexlavka.objects.mapping.get.order.GetOrdersResponse;
import ru.yandex.yandexlavka.serivce.OrderService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderControllerImpl implements OrderController {

    private final CreateOrderRequestValidator createOrderRequestValidator;
    private final OrderService orderService;

    @Autowired
    public OrderControllerImpl(CreateOrderRequestValidator createOrderRequestValidator, OrderService orderService) {
        this.createOrderRequestValidator = createOrderRequestValidator;
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(
            CreateOrderRequest createOrderRequest,
            BindingResult bindingResult
    ) {
        createOrderRequestValidator.validate(createOrderRequest, bindingResult);
        if (bindingResult.hasErrors())
            throw BadRequestException.EMPTY;
        CreateOrderResponse response = orderService.addOrders(createOrderRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetOrderResponse> getOrder(
            Long orderId
    ) {
        GetOrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetOrdersResponse> getOrders(
            Integer offset,
            Integer limit
    ) {
        GetOrdersResponse response = orderService.getOrderRange(offset, limit);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CompleteOrderResponse> completeOrder(
            CompleteOrderRequestDto completeOrderRequestDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            throw BadRequestException.EMPTY;
        CompleteOrderResponse response = orderService.completeOrders(completeOrderRequestDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<OrderAssignResponse> ordersAssign(
            LocalDate date
    ) {
        if (date == null) date = LocalDate.now();
        OrderAssignResponse responses = orderService.assignOrders(date);
        return ResponseEntity.ok(responses);
    }
}
