package ru.yandex.yandexlavka.objects.mapping.get.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.objects.dto.OrderDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderResponse {
    OrderDto order;
}
