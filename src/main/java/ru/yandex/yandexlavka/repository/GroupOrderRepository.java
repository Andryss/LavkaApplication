package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.GroupOrdersEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupOrderRepository extends JpaRepository<GroupOrdersEntity, Long> {
    @Transactional(readOnly = true)
    List<GroupOrdersEntity> findAllByAssignedCourierAndCompleteTimeGreaterThanEqualAndCompleteTimeLessThan(CourierEntity assignedCourier, LocalDateTime startTime, LocalDateTime endTime);
}
