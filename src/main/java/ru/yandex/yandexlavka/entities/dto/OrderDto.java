package ru.yandex.yandexlavka.entities.dto;

import java.util.Date;

public class OrderDto {
    Long order_id;
    Float weight;
    Integer regions;
    String[] delivery_hours;
    Integer cost;
    Date completed_time;
}
