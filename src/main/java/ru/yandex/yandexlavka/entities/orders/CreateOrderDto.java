package ru.yandex.yandexlavka.entities.orders;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDto {
    @NotNull
    Float weight;
    @NotNull
    Integer regions;
    @NotNull
    List<String> deliveryHours; // TODO: validate
    @NotNull
    Integer cost;
}
