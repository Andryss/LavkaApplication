package ru.andryss.lavka.serivce.rating;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.andryss.lavka.controller.CourierController;
import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.entity.OrderEntity;

import java.time.LocalDate;
import java.util.List;

public interface CourierRatingService {

    /**
     * Counts and returns rating and earnings of the courier
     * @param courier courier to calculate rating about
     * @param startDate start of the interval from which to count rating
     * @param endDate end of that interval
     * @param completedOrders orders completed by given courier during given interval
     * @return courier meta-info during given interval
     * @see CourierController
     */
    CourierMetaInfo getCourierMetaInfo(CourierEntity courier, LocalDate startDate, LocalDate endDate, List<OrderEntity> completedOrders);

    @Getter
    @AllArgsConstructor
    class CourierMetaInfo {
        Integer rating;
        Integer earnings;
    }
}
