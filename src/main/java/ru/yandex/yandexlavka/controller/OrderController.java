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

    /**
     * Loads orders into the system
     * @param createOrderRequest info about orders to load
     * @param bindingResult object containing errors during parsing and validating orders info
     * @return added orders with assigned IDs
     */
    @PostMapping
    ResponseEntity<List<OrderDto>> createOrder( // FIXME: why not "CreateOrderResponse"?
            @RequestBody @Valid CreateOrderRequest createOrderRequest,
            BindingResult bindingResult
    );

    /**
     * Returns info about order
     * @param orderId order id to get info about
     * @return order info
     */
    @GetMapping("/{order_id}")
    ResponseEntity<OrderDto> getOrder(
            @PathVariable(name = "order_id") Long orderId
    );

    /**
     * Returns info about all orders
     * @param offset number of orders to skip from start, default is 0 (start position)
     * @param limit numbers of orders to return, default is 1 (slice size)
     * @return list of orders info
     */
    @GetMapping
    ResponseEntity<List<OrderDto>> getOrders( // FIXME: why not "GetOrdersResponse"?
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer offset,
            @RequestParam(defaultValue = "1") @Positive Integer limit
    );

    /**
     * Completes orders (set completed time)
     * @param completeOrderRequestDto info about what courier, what time, and what order
     * @param bindingResult object containing errors during parsing and validating info object
     * @return list of completed orders with completed time
     */
    @PostMapping("/complete")
    ResponseEntity<List<OrderDto>> completeOrder( // FIXME: why not "CompleteOrdersResponse"?
            @RequestBody @Valid CompleteOrderRequestDto completeOrderRequestDto,
            BindingResult bindingResult
    );

    /**
     * Splits orders into groups and assign that groups to couriers
     * @param date assignment date
     * @return order groups assigned to couriers
     */
    @PostMapping("/assign")
    ResponseEntity<List<OrderAssignResponse>> ordersAssign( // FIXME: why List<>? Singleton list :)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date // FIXME: data is deprecated? seriously?
    );

}
