package ru.yandex.yandexlavka.serivce.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;
import ru.yandex.yandexlavka.serivce.rating.util.EarningCalculator;
import ru.yandex.yandexlavka.serivce.rating.util.RatingCalculator;

import java.time.LocalDate;
import java.util.List;

@Service
public class CourierRatingServiceImpl implements CourierRatingService {

    private final EarningCalculator earningsCalculator;
    private final RatingCalculator ratingCalculator;

    @Autowired
    public CourierRatingServiceImpl(EarningCalculator earningsCalculator, RatingCalculator ratingCalculator) {
        this.earningsCalculator = earningsCalculator;
        this.ratingCalculator = ratingCalculator;
    }

    public CourierMetaInfo getCourierMetaInfo(CourierEntity courier, LocalDate startDate, LocalDate endDate, List<OrderEntity> completedOrders) {
        return new CourierMetaInfo(
                ratingCalculator.calculate(courier, startDate, endDate, completedOrders),
                earningsCalculator.calculate(courier, startDate, endDate, completedOrders)
        );
    }
}
