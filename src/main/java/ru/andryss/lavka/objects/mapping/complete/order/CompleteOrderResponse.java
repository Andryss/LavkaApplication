package ru.andryss.lavka.objects.mapping.complete.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.andryss.lavka.objects.dto.OrderDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteOrderResponse {
    List<OrderDto> orders;
}
