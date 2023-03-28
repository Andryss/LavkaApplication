package ru.yandex.yandexlavka.entities;

import ru.yandex.yandexlavka.entities.dto.CourierDto;

import java.util.List;

public class GetCouriersResponse {
    List<CourierDto> couriers;
    Integer limit;
    Integer offset;
}
