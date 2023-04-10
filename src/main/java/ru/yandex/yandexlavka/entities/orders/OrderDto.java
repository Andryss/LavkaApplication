package ru.yandex.yandexlavka.entities.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    Long orderId;
    Float weight;
    Integer regions;
    List<String> deliveryHours;
    Integer cost;
    Date completedTime; // TODO: do i need to return it if null?
}
