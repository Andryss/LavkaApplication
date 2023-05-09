package ru.yandex.yandexlavka.serivce.rating.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Component
public class RatingCalculatorImpl implements RatingCalculator {

    private final RatingCoefficientResolver ratingCoefficientResolver;

    @Autowired
    public RatingCalculatorImpl(RatingCoefficientResolver ratingCoefficientResolver) {
        this.ratingCoefficientResolver = ratingCoefficientResolver;
    }

    @Override
    public int calculate(CourierEntity courier, LocalDate startDate, LocalDate endDate, List<OrderEntity> completedOrders) {
        int completedOrdersCount = completedOrders.size();
        long hoursAmount = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toHours();
        int coefficient = ratingCoefficientResolver.resolve(courier);
        return (int) (((double) completedOrdersCount / hoursAmount) * coefficient);
    }
}
