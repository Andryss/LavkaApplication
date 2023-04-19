package ru.yandex.yandexlavka.objects.mapping.assign.order;

import java.time.LocalDate;
import java.util.List;

public class OrderAssignResponse {
    LocalDate date;
    List<CouriersGroupOrders> couriers;
}
