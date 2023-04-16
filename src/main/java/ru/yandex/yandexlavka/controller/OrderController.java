package ru.yandex.yandexlavka.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.controller.validator.CreateOrderRequestValidator;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.exception.BadRequestException;
import ru.yandex.yandexlavka.exception.NotFoundException;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.serivce.OrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final CreateOrderRequestValidator createOrderRequestValidator;
    private final OrderService orderService;

    @Autowired
    public OrderController(CreateOrderRequestValidator createOrderRequestValidator, OrderService orderService) {
        this.createOrderRequestValidator = createOrderRequestValidator;
        this.orderService = orderService;
    }

    @PostMapping
    ResponseEntity<List<OrderDto>> createOrder(
            @RequestBody @Valid CreateOrderRequest createOrderRequest,
            BindingResult bindingResult
    ) {
        createOrderRequestValidator.validate(createOrderRequest, bindingResult);
        if (bindingResult.hasErrors())
            throw BadRequestException.EMPTY;
        List<OrderDto> response = orderService.addOrders(createOrderRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{order_id}")
    ResponseEntity<OrderDto> getOrder(
            @PathVariable(name = "order_id") Long orderId
    ) {
        Optional<OrderDto> orderById = orderService.getOrderById(orderId);
        if (orderById.isEmpty())
            throw NotFoundException.EMPTY;
        return ResponseEntity.ok(orderById.get());
    }

    @GetMapping
    ResponseEntity<List<OrderDto>> getOrders(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer offset,
            @RequestParam(defaultValue = "1") @Positive Integer limit
    ) {
        List<OrderDto> response = orderService.getOrderRange(offset, limit);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    ResponseEntity<List<OrderDto>> completeOrder(
            @RequestBody @Valid CompleteOrderRequestDto completeOrderRequestDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            throw BadRequestException.EMPTY;
        List<OrderDto> response = orderService.completeOrders(completeOrderRequestDto);
        return ResponseEntity.ok(response);
    }
}
