package ru.yandex.yandexlavka.entities;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.entities.orders.CompleteOrder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteOrderRequestDto {
    @NotNull
    List<CompleteOrder> completeInfo;
}
