package ru.yandex.yandexlavka.entities.orders;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDto {
    @NotNull
    @Positive
    Float weight;

    @NotNull
    Integer regions;

    @NotNull
    List<String> deliveryHours;

    @NotNull
    @Positive
    Integer cost;
}
