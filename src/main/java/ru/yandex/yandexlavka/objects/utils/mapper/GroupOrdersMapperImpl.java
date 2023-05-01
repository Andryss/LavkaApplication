package ru.yandex.yandexlavka.objects.utils.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.entity.GroupOrdersEntity;
import ru.yandex.yandexlavka.objects.mapping.assign.order.GroupOrders;

import java.util.List;

@Component
public class GroupOrdersMapperImpl implements GroupOrdersMapper {

    private final OrderMapper orderMapper;

    @Autowired
    public GroupOrdersMapperImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public GroupOrders mapGroupOrders(GroupOrdersEntity groupOrdersEntity) {
        return new GroupOrders(
                groupOrdersEntity.getGroupId(),
                orderMapper.mapOrderDtoList(groupOrdersEntity.getOrders())
        );
    }

    @Override
    public List<GroupOrders> mapGroupOrdersList(List<GroupOrdersEntity> groupOrdersEntityList) {
        return groupOrdersEntityList.stream().map(this::mapGroupOrders).toList();
    }
}
