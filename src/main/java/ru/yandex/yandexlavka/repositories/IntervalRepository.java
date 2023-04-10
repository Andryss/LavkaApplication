package ru.yandex.yandexlavka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entities.IntervalEntity;

import java.sql.Time;
import java.util.Optional;

@Repository
public interface IntervalRepository extends JpaRepository<IntervalEntity, Long> {
    Optional<IntervalEntity> findByStartTimeAndEndTime(Time startTime, Time endTime);
}
