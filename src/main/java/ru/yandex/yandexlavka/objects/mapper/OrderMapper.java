package ru.yandex.yandexlavka.objects.mapper;

import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderDto;

public interface OrderMapper {

    OrderEntity mapOrderEntity(CreateOrderDto createOrderDto);

    OrderDto mapOrderDto(OrderEntity orderEntity);

}
