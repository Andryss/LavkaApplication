package ru.yandex.yandexlavka.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.entities.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.entities.orders.CreateOrderRequest;
import ru.yandex.yandexlavka.entities.orders.OrderDto;
import ru.yandex.yandexlavka.entities.exceptions.BadRequestException;
import ru.yandex.yandexlavka.entities.exceptions.BadRequestResponse;
import ru.yandex.yandexlavka.entities.exceptions.NotFoundException;
import ru.yandex.yandexlavka.entities.exceptions.NotFoundResponse;
import ru.yandex.yandexlavka.serivces.OrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    ResponseEntity<List<OrderDto>> createOrder(
            @RequestBody @Valid CreateOrderRequest createOrderRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            throw new BadRequestException();
        // TODO: add orders delivery hours validation
        List<OrderDto> response = orderService.addOrders(createOrderRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{order_id}")
    ResponseEntity<OrderDto> getOrder(
            @PathVariable(name = "order_id") Long orderId
    ) {
        Optional<OrderDto> orderById = orderService.getOrderById(orderId);
        if (orderById.isEmpty())
            throw new NotFoundException();
        return ResponseEntity.ok(orderById.get());
    }

    @GetMapping
    ResponseEntity<List<OrderDto>> getOrders(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "1") Integer limit
    ) {
        if (offset < 0 || limit < 1)
            throw new BadRequestException();
        List<OrderDto> response = orderService.getOrderRange(offset, limit);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    ResponseEntity<List<OrderDto>> completeOrder(
            @RequestBody @Valid CompleteOrderRequestDto completeOrderRequestDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            throw new BadRequestException();
        // TODO: add request simple validation
        List<OrderDto> response = orderService.completeOrders(completeOrderRequestDto);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler({ BadRequestException.class })
    ResponseEntity<BadRequestResponse> handleBadRequest() {
        return ResponseEntity.badRequest().body(new BadRequestResponse());
    }

    @ExceptionHandler({ NotFoundException.class })
    ResponseEntity<NotFoundResponse> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse());
    }
}
