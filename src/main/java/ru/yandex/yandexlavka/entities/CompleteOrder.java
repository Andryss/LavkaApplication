package ru.yandex.yandexlavka.entities;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteOrder {
    @NotNull
    Long courierId;
    @NotNull
    Long orderId;
    @NotNull
    Date completeTime; // TODO: check parsing String->Date
}
