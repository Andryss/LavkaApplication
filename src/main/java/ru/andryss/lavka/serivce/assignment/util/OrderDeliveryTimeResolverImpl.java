package ru.andryss.lavka.serivce.assignment.util;

import org.springframework.stereotype.Component;
import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.entity.GroupOrdersEntity;
import ru.andryss.lavka.objects.entity.OrderEntity;

import java.util.List;
import java.util.Objects;

@Component
public class OrderDeliveryTimeResolverImpl implements OrderDeliveryTimeResolver {
    @Override
    public int resolve(CourierEntity courierEntity, GroupOrdersEntity groupOrdersEntity, OrderEntity orderEntity) {
        List<OrderEntity> orders = groupOrdersEntity.getOrders();
        if (orders.size() == 0) return firstOrderFirstRegion(courierEntity);

        OrderEntity lastOrder = orders.get(orders.size() - 1);
        Integer lastOrderRegionNumber = lastOrder.getRegions().getRegionNumber();

        if (!Objects.equals(orderEntity.getRegions().getRegionNumber(), lastOrderRegionNumber))
            return firstOrderNonFirstRegion(courierEntity);

        boolean isFirstRegion = true;
        for (int i = orders.size() - 2; i >= 0; i--) {
            if (!Objects.equals(orders.get(i).getRegions().getRegionNumber(), lastOrderRegionNumber)) {
                isFirstRegion = false;
                break;
            }
        }

        if (isFirstRegion) return nonFirstOrderFirstRegion(courierEntity);
        return nonFirstOrderNonFirstRegion(courierEntity);
    }

    private int firstOrderFirstRegion(CourierEntity courierEntity) {
        return switch (courierEntity.getCourierType()) {
            case FOOT -> 25;
            case BIKE -> 12;
            case AUTO -> 8;
        };
    }

    private int nonFirstOrderFirstRegion(CourierEntity courierEntity) {
        return switch (courierEntity.getCourierType()) {
            case FOOT -> 10;
            case BIKE -> 8;
            case AUTO -> 4;
        };
    }

    private int firstOrderNonFirstRegion(CourierEntity courierEntity) {
        return switch (courierEntity.getCourierType()) {
            case FOOT -> -1;
            case BIKE -> 12;
            case AUTO -> 8;
        };
    }

    private int nonFirstOrderNonFirstRegion(CourierEntity courierEntity) {
        return switch (courierEntity.getCourierType()) {
            case FOOT -> -1;
            case BIKE -> 8;
            case AUTO -> 4;
        };
    }
}
