package ru.yandex.yandexlavka.objects.mapping.create.courier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.objects.dto.CourierDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCouriersResponse {
    List<CourierDto> couriers;
}
