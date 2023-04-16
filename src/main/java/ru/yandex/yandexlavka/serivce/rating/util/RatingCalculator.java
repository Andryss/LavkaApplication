package ru.yandex.yandexlavka.serivce.rating.util;

import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;

import java.time.LocalDate;
import java.util.List;

public interface RatingCalculator {
    int calculate(CourierEntity courier, LocalDate startDate, LocalDate endDate, List<OrderEntity> completedOrders);
}
