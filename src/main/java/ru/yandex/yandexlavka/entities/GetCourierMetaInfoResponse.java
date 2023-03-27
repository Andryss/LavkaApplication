package ru.yandex.yandexlavka.entities;

public class GetCourierMetaInfoResponse {
    Long courier_id;
    CourierType courier_type;
    int[] regions;
    String[] working_hours;
    Integer rating;
    Integer earnings;
}
