package ru.yandex.yandexlavka.entities;

import ru.yandex.yandexlavka.entities.orders.GroupOrders;

import java.util.List;

public class CouriersGroupOrders {
    Long courierId;
    List<GroupOrders> orders;
}
