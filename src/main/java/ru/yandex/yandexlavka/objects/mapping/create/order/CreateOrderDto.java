package ru.yandex.yandexlavka.objects.mapping.create.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Positive
    Integer regions;

    @NotEmpty
    List<@Pattern(regexp = "^([0-1]\\d|2[0-3]):[0-5]\\d-([0-1]\\d|2[0-3]):[0-5]\\d$") String> deliveryHours;

    @NotNull
    @Positive
    Integer cost;
}
