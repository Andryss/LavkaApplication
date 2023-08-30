package ru.andryss.lavka.objects.utils.mapper;

import ru.andryss.lavka.objects.dto.OrderDto;
import ru.andryss.lavka.objects.entity.OrderEntity;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderDto;

import java.util.List;

public interface OrderMapper {

    OrderEntity mapOrderEntity(CreateOrderDto createOrderDto);

    OrderDto mapOrderDto(OrderEntity orderEntity);

    List<OrderDto> mapOrderDtoList(List<OrderEntity> orderEntityList);

}
