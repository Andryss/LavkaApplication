package ru.yandex.yandexlavka.serivce.assignment.util;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;

@Component
public class MaxOrdersAmountResolverImpl implements MaxOrdersAmountResolver {
    @Override
    public int resolve(CourierEntity courierEntity) {
        return switch (courierEntity.getCourierType()){
            case FOOT -> 2;
            case BIKE -> 4;
            case AUTO -> 7;
        };
    }
}
