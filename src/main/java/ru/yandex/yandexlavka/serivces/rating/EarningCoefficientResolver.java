package ru.yandex.yandexlavka.serivces.rating;

import ru.yandex.yandexlavka.entities.couriers.CourierEntity;

interface EarningCoefficientResolver {
    int resolve(CourierEntity courier);
}
