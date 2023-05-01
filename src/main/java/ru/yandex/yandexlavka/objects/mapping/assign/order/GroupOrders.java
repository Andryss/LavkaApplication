package ru.yandex.yandexlavka.objects.mapping.assign.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.objects.dto.OrderDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupOrders {
    Long groupOrderId;
    List<OrderDto> orders;
}
