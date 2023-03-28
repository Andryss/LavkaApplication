package ru.yandex.yandexlavka.entities.dto;

import java.util.Date;
import java.util.List;

public class OrderDto {
    Long orderId;
    Float weight;
    Integer regions;
    List<String> deliveryHours;
    Integer cost;
    Date completedTime;
}
