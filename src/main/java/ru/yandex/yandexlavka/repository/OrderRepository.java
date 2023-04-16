package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByOrderIdIn(List<Long> idList);
    List<OrderEntity> findAllByAssignedCourierIdAndCompletedTimeGreaterThanEqualAndCompletedTimeLessThan(CourierEntity courierId, LocalDate startDate, LocalDate endDate);
}
