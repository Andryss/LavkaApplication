package ru.yandex.yandexlavka.entities;

import ru.yandex.yandexlavka.entities.dto.CourierDto;

public class GetCouriersResponse {
    CourierDto[] couriers;
    Integer limit;
    Integer offset;
}
