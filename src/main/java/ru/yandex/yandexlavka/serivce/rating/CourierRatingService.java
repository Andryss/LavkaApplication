package ru.yandex.yandexlavka.serivce.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;

import java.time.LocalDate;
import java.util.List;

public interface CourierRatingService {

    CourierMetaInfo getCourierMetaInfo(CourierEntity courier, LocalDate startDate, LocalDate endDate, List<OrderEntity> completedOrders);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class CourierMetaInfo {
        Integer rating;
        Integer earnings;
    }
}
