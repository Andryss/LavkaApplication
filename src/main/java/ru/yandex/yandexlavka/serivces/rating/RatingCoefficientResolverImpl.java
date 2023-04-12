package ru.yandex.yandexlavka.serivces.rating;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.entities.couriers.CourierEntity;

@Component
public class RatingCoefficientResolverImpl implements RatingCoefficientResolver {
    @Override
    public int resolve(CourierEntity courierEntity) {
        return switch (courierEntity.getCourierType()) {
            case FOOT -> 3;
            case BIKE -> 2;
            case AUTO -> 1;
        };
    }
}
