package ru.yandex.yandexlavka.entities.orders;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteOrder {
    @NotNull
    Long courierId;

    @NotNull
    Long orderId;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate completeTime; // TODO: check parsing String->Date
}
