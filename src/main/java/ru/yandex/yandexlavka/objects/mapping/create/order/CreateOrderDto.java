package ru.yandex.yandexlavka.objects.mapping.create.order;

import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    List<String> deliveryHours;

    @NotNull
    @Positive
    Integer cost;
}
