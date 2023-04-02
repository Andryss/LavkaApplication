package ru.yandex.yandexlavka.entities.couriers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierDto {
    Long courierId;
    CourierType courierType;
    List<Integer> regions;
    List<String> workingHours;

    public CourierDto(CreateCourierDto createCourierDto) {
        courierType = createCourierDto.courierType;
        regions = createCourierDto.regions;
        workingHours = createCourierDto.workingHours;
    }
}
