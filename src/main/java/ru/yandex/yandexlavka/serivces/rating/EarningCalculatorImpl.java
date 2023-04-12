package ru.yandex.yandexlavka.serivces.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.entities.couriers.CourierEntity;
import ru.yandex.yandexlavka.entities.orders.OrderEntity;

import java.time.LocalDate;
import java.util.List;

@Component
public class EarningCalculatorImpl implements EarningCalculator {

    private final EarningCoefficientResolver earningCoefficientResolver;

    @Autowired
    public EarningCalculatorImpl(EarningCoefficientResolver earningCoefficientResolver) {
        this.earningCoefficientResolver = earningCoefficientResolver;
    }

    @Override
    public int calculate(CourierEntity courier, LocalDate startDate, LocalDate endDate, List<OrderEntity> completedOrders) {
        int costsSum = completedOrders.stream()
                .mapToInt(OrderEntity::getCost)
                .sum();
        int coefficient = earningCoefficientResolver.resolve(courier);
        return costsSum * coefficient;
    }
}
