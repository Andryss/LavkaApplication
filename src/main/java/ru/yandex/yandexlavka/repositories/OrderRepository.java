package ru.yandex.yandexlavka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entities.couriers.CourierEntity;
import ru.yandex.yandexlavka.entities.orders.OrderEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByOrderIdIn(List<Long> idList);
    List<OrderEntity> findAllByAssignedCourierIdAndCompletedTimeGreaterThanEqualAndCompletedTimeLessThan(CourierEntity courierId, LocalDate startDate, LocalDate endDate);
}
