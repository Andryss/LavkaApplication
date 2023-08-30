package ru.andryss.lavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.entity.GroupOrdersEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GroupOrderRepository extends JpaRepository<GroupOrdersEntity, Long> {
    @Transactional(readOnly = true)
    List<GroupOrdersEntity> findAllByAssignedCourierAndAssignedDateGreaterThanEqualAndAssignedDateLessThan(CourierEntity assignedCourier, LocalDate startDate, LocalDate endDate);

    @Transactional(readOnly = true)
    boolean existsByAssignedDateEquals(LocalDate assignedDate);
}
