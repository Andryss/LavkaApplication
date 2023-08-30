package ru.andryss.lavka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.lavka.exception.BadRequestException;
import ru.andryss.lavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrderResponse;
import ru.andryss.lavka.controller.validator.CreateOrderRequestValidator;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderRequest;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderResponse;
import ru.andryss.lavka.objects.mapping.get.order.GetOrderResponse;
import ru.andryss.lavka.objects.mapping.get.order.GetOrdersResponse;
import ru.andryss.lavka.serivce.OrderService;

import java.time.LocalDate;

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
            throw new BadRequestException(bindingResult.getAllErrors().toString());
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
            throw new BadRequestException(bindingResult.getAllErrors().toString());
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
