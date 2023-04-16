package ru.yandex.yandexlavka.objects.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderDto;
import ru.yandex.yandexlavka.objects.dto.OrderDto;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;

@Component
public class OrderMapper {

    private final IntervalMapper intervalMapper;
    private final RegionMapper regionMapper;

    public OrderMapper(IntervalMapper intervalMapper, RegionMapper regionMapper) {
        this.intervalMapper = intervalMapper;
        this.regionMapper = regionMapper;
    }

    public OrderDto mapOrderDto(CreateOrderDto createOrderDto){
        return new OrderDto(
                null,
                createOrderDto.getWeight(),
                createOrderDto.getRegions(),
                createOrderDto.getDeliveryHours(),
                createOrderDto.getCost(),
                null
        );
    }

    public OrderEntity mapOrderEntity(CreateOrderDto createOrderDto) {
        return new OrderEntity(
                null,
                createOrderDto.getWeight(),
                createOrderDto.getCost(),
                null,
                null,
                regionMapper.mapRegionEntity(createOrderDto.getRegions()),
                intervalMapper.mapIntervalEntityList(createOrderDto.getDeliveryHours())
        );
    }

    public OrderDto mapOrderDto(OrderEntity orderEntity) {
        return new OrderDto(
                orderEntity.getOrderId(),
                orderEntity.getWeight(),
                regionMapper.mapRegionNumber(orderEntity.getRegions()),
                intervalMapper.mapIntervalStringList(orderEntity.getDeliveryHours()),
                orderEntity.getCost(),
                orderEntity.getCompletedTime()
        );
    }

}
