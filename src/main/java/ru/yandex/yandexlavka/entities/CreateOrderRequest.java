package ru.yandex.yandexlavka.entities;

import jakarta.validation.constraints.NotNull;
import ru.yandex.yandexlavka.entities.dto.CreateOrderDto;

public class CreateOrderRequest {
    CreateOrderDto[] orders;
}
