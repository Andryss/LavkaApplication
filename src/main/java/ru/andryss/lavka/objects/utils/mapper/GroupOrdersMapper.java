package ru.andryss.lavka.objects.utils.mapper;

import ru.andryss.lavka.objects.mapping.assign.order.GroupOrders;
import ru.andryss.lavka.objects.entity.GroupOrdersEntity;

import java.util.List;

public interface GroupOrdersMapper {

    GroupOrders mapGroupOrders(GroupOrdersEntity groupOrdersEntity);

    List<GroupOrders> mapGroupOrdersList(List<GroupOrdersEntity> groupOrdersEntityList);

}
