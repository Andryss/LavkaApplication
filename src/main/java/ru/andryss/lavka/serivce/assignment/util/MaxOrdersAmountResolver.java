package ru.andryss.lavka.serivce.assignment.util;

import ru.andryss.lavka.objects.entity.CourierEntity;

public interface MaxOrdersAmountResolver {
    int resolve(CourierEntity courierEntity);
}
