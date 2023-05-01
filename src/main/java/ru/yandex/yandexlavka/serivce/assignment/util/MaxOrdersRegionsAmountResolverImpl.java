package ru.yandex.yandexlavka.serivce.assignment.util;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;

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
