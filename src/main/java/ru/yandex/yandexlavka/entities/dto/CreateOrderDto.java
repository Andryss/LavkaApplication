package ru.yandex.yandexlavka.entities.dto;

import java.util.List;

public class CreateOrderDto {
    Float weight;
    Integer regions;
    List<String> deliveryHours;
    Integer cost;
}
