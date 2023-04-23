package ru.yandex.yandexlavka.objects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    Long orderId;
    Float weight;
    Integer regions; // FIXME: why not "region"?
    List<String> deliveryHours;
    Integer cost;
    LocalDateTime completedTime;
}
