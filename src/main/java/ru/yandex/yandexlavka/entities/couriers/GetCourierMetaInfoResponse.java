package ru.yandex.yandexlavka.entities.couriers;

import java.util.List;

public class GetCourierMetaInfoResponse {
    Long courierId;
    CourierType courierType;
    List<Integer> regions;
    List<String> workingHours;
    Integer rating;
    Integer earnings;
}
