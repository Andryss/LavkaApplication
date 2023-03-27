package ru.yandex.yandexlavka.entities.dto;

import ru.yandex.yandexlavka.entities.CourierType;

public class CreateCourierDto {
    CourierType courier_type;
    int[] regions;
    String[] working_hours;
}
