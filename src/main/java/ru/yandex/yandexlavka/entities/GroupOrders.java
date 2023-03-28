package ru.yandex.yandexlavka.entities;

import ru.yandex.yandexlavka.entities.dto.OrderDto;

import java.util.List;

public class GroupOrders {
    Long groupOrderId;
    List<OrderDto> orders;
}
