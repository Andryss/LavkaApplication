package ru.yandex.yandexlavka.serivce.rating.util;

import ru.yandex.yandexlavka.objects.entity.CourierEntity;

interface EarningCoefficientResolver {
    int resolve(CourierEntity courier);
}
