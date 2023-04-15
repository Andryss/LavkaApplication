package ru.yandex.yandexlavka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entities.IntervalEntity;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IntervalRepository extends JpaRepository<IntervalEntity, Long> {
    Optional<IntervalEntity> findByStartTimeAndEndTime(LocalTime startTime, LocalTime endTime);
    Set<IntervalEntity> findAllByStartTimeInAndEndTimeIn(Set<LocalTime> startTimeList, Set<LocalTime> endTimeList);
}
