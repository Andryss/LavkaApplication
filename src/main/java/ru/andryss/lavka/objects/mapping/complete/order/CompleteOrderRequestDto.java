package ru.andryss.lavka.objects.mapping.complete.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteOrderRequestDto { // TODO: CompleteOrderRequest
    @NotEmpty
    List<@NotNull @Valid CompleteOrder> completeInfo;
}
