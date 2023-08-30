package ru.andryss.lavka.objects.utils.mapper;

import org.springframework.stereotype.Component;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderDto;
import ru.andryss.lavka.objects.dto.OrderDto;
import ru.andryss.lavka.objects.entity.OrderEntity;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderMapperImpl implements OrderMapper {

    private final IntervalMapper intervalMapper;
    private final RegionMapper regionMapper;

    public OrderMapperImpl(IntervalMapper intervalMapper, RegionMapper regionMapper) {
        this.intervalMapper = intervalMapper;
        this.regionMapper = regionMapper;
    }

    @Override
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

    @Override
    public OrderDto mapOrderDto(OrderEntity orderEntity) {
        LocalDateTime completedTime = orderEntity.getCompletedTime();
        return new OrderDto(
                orderEntity.getOrderId(),
                orderEntity.getWeight(),
                regionMapper.mapRegionNumber(orderEntity.getRegions()),
                intervalMapper.mapIntervalStringList(orderEntity.getDeliveryHours()),
                orderEntity.getCost(),
                (completedTime == null ? null : completedTime.toString())
        );
    }

    @Override
    public List<OrderDto> mapOrderDtoList(List<OrderEntity> orderEntityList) {
        return orderEntityList.stream().map(this::mapOrderDto).toList();
    }

}
