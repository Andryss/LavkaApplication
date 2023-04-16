package ru.yandex.yandexlavka.serivce.rating.util;

import ru.yandex.yandexlavka.objects.entity.CourierEntity;

public interface RatingCoefficientResolver {
    int resolve(CourierEntity courierEntity);
}
