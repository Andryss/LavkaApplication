package ru.yandex.yandexlavka.serivce.assignment.util;

import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.GroupOrdersEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;

public interface OrderDeliveryTimeResolver {
    int resolve(CourierEntity courierEntity, GroupOrdersEntity groupOrdersEntity, OrderEntity orderEntity);
}
