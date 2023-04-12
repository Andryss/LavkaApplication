package ru.yandex.yandexlavka.entities.couriers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCourierMetaInfoResponse {
    Long courierId;
    CourierType courierType;
    List<Integer> regions;
    List<String> workingHours;
    Integer rating;
    Integer earnings;
}
