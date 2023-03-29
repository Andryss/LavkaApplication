package ru.yandex.yandexlavka.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.entities.*;
import ru.yandex.yandexlavka.entities.dto.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.entities.dto.OrderDto;
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
    ResponseEntity<?> createOrder(
            @RequestBody @Valid CreateOrderRequest createOrderRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(new BadRequestResponse());
        List<OrderDto> response = orderService.addOrders(createOrderRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{order_id}")
    ResponseEntity<?> getOrder(
            @PathVariable(name = "order_id") Long orderId
    ) {
        Optional<OrderDto> orderById = orderService.getOrderById(orderId);
        if (orderById.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse());
        return ResponseEntity.ok(orderById.get());
    }

    @GetMapping
    ResponseEntity<?> getOrders(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "1") Integer limit
    ) {
        if (offset < 0 || limit < 1)
            return ResponseEntity.badRequest().body(new BadRequestResponse());
        List<OrderDto> response = orderService.getOrderRange(offset, limit);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    ResponseEntity<?> completeOrder(
            @RequestBody @Valid CompleteOrderRequestDto completeOrderRequestDto,
            BindingResult bindingResult
    ) { // TODO: what should I do if some Ids are incorrect? (return bad request or set only valid)
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(new BadRequestResponse());
        List<OrderDto> response = orderService.completeOrders(completeOrderRequestDto);
        return ResponseEntity.ok(response);
    }
}
