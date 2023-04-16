package ru.yandex.yandexlavka.entities.couriers;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourierDto {
    @NotNull
    CourierType courierType;

    @NotEmpty
    List<Integer> regions;

    @NotEmpty
    List<String> workingHours;
}
