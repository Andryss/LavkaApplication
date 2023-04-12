package ru.yandex.yandexlavka.serivces.rating;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.entities.couriers.CourierEntity;

@Component
class EarningCoefficientResolverImpl implements EarningCoefficientResolver {
    @Override
    public int resolve(CourierEntity courier) {
        return switch (courier.getCourierType()) {
            case FOOT -> 2;
            case BIKE -> 3;
            case AUTO -> 4;
        };
    }
}
