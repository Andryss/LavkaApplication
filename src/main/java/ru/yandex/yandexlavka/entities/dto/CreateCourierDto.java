package ru.yandex.yandexlavka.entities.dto;

import ru.yandex.yandexlavka.entities.CourierType;

import java.util.List;

public class CreateCourierDto {
    CourierType courierType;
    List<Integer> regions;
    List<String> workingHours;
}
