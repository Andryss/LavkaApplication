package ru.yandex.yandexlavka.serivce.assignment.util;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;

@Component
public class MaxOrdersWeightResolverImpl implements MaxOrdersWeightResolver {
    @Override
    public int resolve(CourierEntity courierEntity) {
        return switch (courierEntity.getCourierType()){
            case FOOT -> 10;
            case BIKE -> 20;
            case AUTO -> 40;
        };
    }
}
