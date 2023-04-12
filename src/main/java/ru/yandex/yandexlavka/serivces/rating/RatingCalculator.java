package ru.yandex.yandexlavka.serivces.rating;

import ru.yandex.yandexlavka.entities.couriers.CourierEntity;
import ru.yandex.yandexlavka.entities.orders.OrderEntity;

import java.time.LocalDate;
import java.util.List;

public interface RatingCalculator {
    int calculate(CourierEntity courier, LocalDate startDate, LocalDate endDate, List<OrderEntity> completedOrders);
}
