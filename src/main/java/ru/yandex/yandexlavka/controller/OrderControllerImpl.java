package ru.yandex.yandexlavka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.yandexlavka.controller.validator.CreateOrderRequestValidator;
import ru.yandex.yandexlavka.exception.BadRequestException;
import ru.yandex.yandexlavka.exception.NotFoundException;
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.mapping.complete.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderRequest;
import ru.yandex.yandexlavka.serivce.OrderService;

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
    public ResponseEntity<List<OrderDto>> createOrder(
            CreateOrderRequest createOrderRequest,
            BindingResult bindingResult
    ) {
        createOrderRequestValidator.validate(createOrderRequest, bindingResult);
        if (bindingResult.hasErrors())
            throw BadRequestException.EMPTY;
        List<OrderDto> response = orderService.addOrders(createOrderRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<OrderDto> getOrder(
            Long orderId
    ) {
        Optional<OrderDto> orderById = orderService.getOrderById(orderId);
        if (orderById.isEmpty())
            throw NotFoundException.EMPTY;
        return ResponseEntity.ok(orderById.get());
    }

    @Override
    public ResponseEntity<List<OrderDto>> getOrders(
            Integer offset,
            Integer limit
    ) {
        List<OrderDto> response = orderService.getOrderRange(offset, limit);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<OrderDto>> completeOrder(
            CompleteOrderRequestDto completeOrderRequestDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            throw BadRequestException.EMPTY;
        List<OrderDto> response = orderService.completeOrders(completeOrderRequestDto);
        return ResponseEntity.ok(response);
    }
}
