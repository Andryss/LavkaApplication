package ru.andryss.lavka.serivce.rating.util;

import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.entity.OrderEntity;

import java.time.LocalDate;
import java.util.List;

public interface RatingCalculator {
    int calculate(CourierEntity courier, LocalDate startDate, LocalDate endDate, List<OrderEntity> completedOrders);
}
