package ru.andryss.lavka.serivce.assignment.util;

import org.springframework.stereotype.Component;
import ru.andryss.lavka.objects.entity.CourierEntity;

@Component
public class MaxOrdersRegionsAmountResolverImpl implements MaxOrdersRegionsAmountResolver {
    @Override
    public int resolve(CourierEntity courierEntity) {
        return switch (courierEntity.getCourierType()) {
            case FOOT -> 1;
            case BIKE -> 2;
            case AUTO -> 3;
        };
    }
}
