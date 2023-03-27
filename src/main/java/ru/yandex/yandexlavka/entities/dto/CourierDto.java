package ru.yandex.yandexlavka.entities.dto;

import ru.yandex.yandexlavka.entities.CourierType;

public class CourierDto {
    Long courier_id;
    CourierType courier_type;
    int[] regions;
    String[] working_hours;
}
