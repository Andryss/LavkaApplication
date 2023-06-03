package ru.yandex.yandexlavka.objects.mapping.create.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.objects.dto.OrderDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponse {
    List<OrderDto> orders;
}
