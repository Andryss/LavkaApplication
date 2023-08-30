package ru.andryss.lavka.serivce.assignment.util;

import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.entity.GroupOrdersEntity;
import ru.andryss.lavka.objects.entity.OrderEntity;

public interface OrderDeliveryTimeResolver {
    int resolve(CourierEntity courierEntity, GroupOrdersEntity groupOrdersEntity, OrderEntity orderEntity);
}
