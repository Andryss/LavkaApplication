package ru.yandex.yandexlavka.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/orders")
@Validated
public interface OrderController {

    @PostMapping
    ResponseEntity<List<OrderDto>> createOrder( // FIXME: why not "CreateOrderResponse"?
            @RequestBody @Valid CreateOrderRequest createOrderRequest,
            BindingResult bindingResult
    );

    @GetMapping("/{order_id}")
    ResponseEntity<OrderDto> getOrder(
            @PathVariable(name = "order_id") Long orderId
    );

    @GetMapping
    ResponseEntity<List<OrderDto>> getOrders( // FIXME: why not "GetOrdersResponse"?
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer offset,
            @RequestParam(defaultValue = "1") @Positive Integer limit
    );

    @PostMapping("/complete")
    ResponseEntity<List<OrderDto>> completeOrder( // FIXME: why not "CompleteOrdersResponse"?
            @RequestBody @Valid CompleteOrderRequestDto completeOrderRequestDto,
            BindingResult bindingResult
    );

    @PostMapping("/assign")
    ResponseEntity<List<OrderAssignResponse>> ordersAssign( // FIXME: why List<>? Singleton list :)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

}
