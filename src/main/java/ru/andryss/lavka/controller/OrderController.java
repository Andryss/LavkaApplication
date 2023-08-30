package ru.andryss.lavka.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.andryss.lavka.objects.mapping.assign.order.OrderAssignResponse;
import ru.andryss.lavka.objects.mapping.complete.order.CompleteOrderResponse;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderRequest;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderResponse;
import ru.andryss.lavka.objects.mapping.get.order.GetOrderResponse;
import ru.andryss.lavka.objects.mapping.get.order.GetOrdersResponse;

import java.time.LocalDate;

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
    ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody @Valid CreateOrderRequest createOrderRequest,
            BindingResult bindingResult
    );

    /**
     * Returns info about some order
     * @param orderId order id to get info about
     * @return order info
     */
    @GetMapping("/{order_id}")
    ResponseEntity<GetOrderResponse> getOrder(
            @PathVariable("order_id") Long orderId
    );

    /**
     * Returns info about all orders
     * @param offset number of orders to skip from start, default is 0 (start position)
     * @param limit numbers of orders to return, default is 1 (slice size)
     * @return list of orders info
     */
    @GetMapping
    ResponseEntity<GetOrdersResponse> getOrders(
            @RequestParam(name = "offset", defaultValue = "0") @PositiveOrZero Integer offset,
            @RequestParam(name = "limit", defaultValue = "1") @Positive Integer limit
    );

    /**
     * Completes orders (set completed time)
     * @param completeOrderRequestDto info about what courier, what time, and what order
     * @param bindingResult object containing errors during parsing and validating info object
     * @return list of completed orders with completed time
     */
    @PostMapping("/complete")
    ResponseEntity<CompleteOrderResponse> completeOrder(
            @RequestBody @Valid CompleteOrderRequestDto completeOrderRequestDto,
            BindingResult bindingResult
    );

    /**
     * Splits orders into groups and assign that groups to couriers
     * @param date assignment date
     * @return order groups assigned to couriers
     */
    @PostMapping("/assign")
    ResponseEntity<OrderAssignResponse> ordersAssign(
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date // data is deprecated? seriously?
    );

}
