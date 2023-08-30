package ru.andryss.lavka.serivce.rating.util;

import ru.andryss.lavka.objects.entity.CourierEntity;

public interface RatingCoefficientResolver {
    int resolve(CourierEntity courierEntity);
}
