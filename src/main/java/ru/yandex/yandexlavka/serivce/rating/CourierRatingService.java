package ru.yandex.yandexlavka.serivce.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;

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
     * @see ru.yandex.yandexlavka.controller.CourierController
     */
    CourierMetaInfo getCourierMetaInfo(CourierEntity courier, LocalDate startDate, LocalDate endDate, List<OrderEntity> completedOrders);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class CourierMetaInfo {
        Integer rating;
        Integer earnings;
    }
}
