package ru.andryss.lavka.objects.mapping.get.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.andryss.lavka.objects.dto.OrderDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderResponse {
    OrderDto order;
}
