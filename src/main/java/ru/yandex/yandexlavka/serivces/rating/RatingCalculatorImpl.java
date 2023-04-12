package ru.yandex.yandexlavka.serivces.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.entities.couriers.CourierEntity;
import ru.yandex.yandexlavka.entities.orders.OrderEntity;

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
        int hoursAmount = startDate.until(endDate).getDays() * 24;
        int coefficient = ratingCoefficientResolver.resolve(courier);
        return (completedOrdersCount / hoursAmount) * coefficient;
    }
}
