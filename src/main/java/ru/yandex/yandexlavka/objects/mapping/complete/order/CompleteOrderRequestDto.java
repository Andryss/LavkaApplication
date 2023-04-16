package ru.yandex.yandexlavka.objects.mapping.complete.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteOrderRequestDto {
    @NotNull
    List<@Valid CompleteOrder> completeInfo;
}
