package ru.andryss.lavka.objects.dto;

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
}
