package ru.yandex.yandexlavka.serivces.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.entities.couriers.CourierEntity;
import ru.yandex.yandexlavka.entities.orders.OrderEntity;

import java.time.LocalDate;
import java.util.List;

@Service
public class CourierRatingService {

    private final EarningCalculator earningsCalculator;
    private final RatingCalculator ratingCalculator;

    @Autowired
    public CourierRatingService(EarningCalculator earningsCalculator, RatingCalculator ratingCalculator) {
        this.earningsCalculator = earningsCalculator;
        this.ratingCalculator = ratingCalculator;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourierMetaInfo {
        Integer rating;
        Integer earnings;
    }

    public CourierMetaInfo getCourierMetaInfo(CourierEntity courier, LocalDate startDate, LocalDate endDate, List<OrderEntity> completedOrders) {
        return new CourierMetaInfo(
                ratingCalculator.calculate(courier, startDate, endDate, completedOrders),
                earningsCalculator.calculate(courier, startDate, endDate, completedOrders)
        );
    }
}
