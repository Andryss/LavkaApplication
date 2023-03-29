package ru.yandex.yandexlavka.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.entities.dto.CourierDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCouriersResponse {
    List<CourierDto> couriers;
}
