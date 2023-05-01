package ru.yandex.yandexlavka.objects.utils.mapper;

import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderDto;

import java.util.List;

public interface OrderMapper {

    OrderEntity mapOrderEntity(CreateOrderDto createOrderDto);

    OrderDto mapOrderDto(OrderEntity orderEntity);

    List<OrderDto> mapOrderDtoList(List<OrderEntity> orderEntityList);

}
