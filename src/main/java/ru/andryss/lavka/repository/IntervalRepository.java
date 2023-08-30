package ru.andryss.lavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.andryss.lavka.objects.entity.IntervalEntity;

import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IntervalRepository extends JpaRepository<IntervalEntity, Long> {
    @Transactional(readOnly = true)
    Optional<IntervalEntity> findByStartTimeAndEndTime(LocalTime startTime, LocalTime endTime);

    @Transactional(readOnly = true)
    Set<IntervalEntity> findAllByStartTimeInAndEndTimeIn(Collection<LocalTime> startTimeList, Collection<LocalTime> endTimeList);
}
