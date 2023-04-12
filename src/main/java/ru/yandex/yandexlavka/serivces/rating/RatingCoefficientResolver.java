package ru.yandex.yandexlavka.serivces.rating;

import ru.yandex.yandexlavka.entities.couriers.CourierEntity;

public interface RatingCoefficientResolver {
    int resolve(CourierEntity courierEntity);
}
