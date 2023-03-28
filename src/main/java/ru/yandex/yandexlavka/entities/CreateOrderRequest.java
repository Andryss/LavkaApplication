package ru.yandex.yandexlavka.entities;

import jakarta.validation.constraints.NotNull;
import ru.yandex.yandexlavka.entities.dto.CreateOrderDto;

import java.util.List;

public class CreateOrderRequest {
    List<CreateOrderDto> orders;
}
