package ru.yandex.yandexlavka.serivce.assignment.util;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;

@Component
public class MaxOrdersWeightResolverImpl implements MaxOrdersWeightResolver {
    @Override
    public float resolve(CourierEntity courierEntity) {
        return switch (courierEntity.getCourierType()){
            case FOOT -> 10.0f;
            case BIKE -> 20.0f;
            case AUTO -> 40.0f;
        };
    }
}
