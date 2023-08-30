package ru.andryss.lavka.serivce.rating.util;

import ru.andryss.lavka.objects.entity.CourierEntity;

interface EarningCoefficientResolver {
    int resolve(CourierEntity courier);
}
