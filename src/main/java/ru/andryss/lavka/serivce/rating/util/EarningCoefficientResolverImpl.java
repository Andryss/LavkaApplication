package ru.andryss.lavka.serivce.rating.util;

import org.springframework.stereotype.Component;
import ru.andryss.lavka.objects.entity.CourierEntity;

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
