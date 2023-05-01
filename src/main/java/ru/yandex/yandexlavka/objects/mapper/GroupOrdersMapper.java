package ru.yandex.yandexlavka.objects.mapper;

import ru.yandex.yandexlavka.objects.entity.GroupOrdersEntity;
import ru.yandex.yandexlavka.objects.mapping.assign.order.GroupOrders;

import java.util.List;

public interface GroupOrdersMapper {

    GroupOrders mapGroupOrders(GroupOrdersEntity groupOrdersEntity);

    List<GroupOrders> mapGroupOrdersList(List<GroupOrdersEntity> groupOrdersEntityList);

}
