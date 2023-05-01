package ru.yandex.yandexlavka.serivce.assignment.util;

import ru.yandex.yandexlavka.objects.entity.CourierEntity;

public interface MaxOrdersAmountResolver {
    int resolve(CourierEntity courierEntity);
}
