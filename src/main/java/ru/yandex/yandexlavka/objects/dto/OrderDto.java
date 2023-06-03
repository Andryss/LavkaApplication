package ru.yandex.yandexlavka.objects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    Long orderId;
    Float weight;
    Integer region;
    List<String> deliveryHours;
    Integer cost;
    String completedTime;
}
