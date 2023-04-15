package ru.yandex.yandexlavka.entities.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.entities.IntervalEntity;
import ru.yandex.yandexlavka.repositories.IntervalRepository;
import ru.yandex.yandexlavka.util.DateTimeParser;

import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class IntervalMapper {

    private final IntervalRepository intervalRepository;
    private final DateTimeParser dateTimeParser;

    @Autowired
    public IntervalMapper(IntervalRepository intervalRepository, DateTimeParser dateTimeParser) {
        this.intervalRepository = intervalRepository;
        this.dateTimeParser = dateTimeParser;
    }

    public IntervalEntity mapIntervalEntity(String intervalString) {
        IntervalEntity intervalFromString = createIntervalFromString(intervalString);
        Optional<IntervalEntity> intervalFromRepository = intervalRepository.findByStartTimeAndEndTime(intervalFromString.getStartTime(), intervalFromString.getEndTime());
        return intervalFromRepository.orElseGet(() -> intervalRepository.save(intervalFromString));
    }

    public List<IntervalEntity> mapIntervalEntityList(List<String> intervalStringList) {
        // Map strings to entities and extract sets of start and end time for database query
        List<IntervalEntity> intervalEntities = intervalStringList.stream().map(this::createIntervalFromString).toList();
        Set<LocalTime> startTimeSet = intervalEntities.stream().map(IntervalEntity::getStartTime).collect(Collectors.toSet());
        Set<LocalTime> endTimeSet = intervalEntities.stream().map(IntervalEntity::getEndTime).collect(Collectors.toSet());

        // Fetch intervals with interval boundaries in given list and construct Map<startTime,Map<endTime,entity>>
        Map<LocalTime, Map<LocalTime, IntervalEntity>> intervalEntitiesFromRepository = intervalRepository.findAllByStartTimeInAndEndTimeIn(startTimeSet, endTimeSet).stream()
                .collect(Collectors.groupingBy(IntervalEntity::getStartTime, Collectors.toMap(IntervalEntity::getEndTime, Function.identity())));

        // Find intervals, which wasn't found in repository and create list with intervals entities to save
        List<IntervalEntity> intervalEntityToSave = new ArrayList<>(intervalStringList.size());
        intervalEntities.forEach(interval -> {
            Map<LocalTime, IntervalEntity> endTimeMap = intervalEntitiesFromRepository.get(interval.getStartTime());
            if (endTimeMap == null) {
                intervalEntityToSave.add(interval);
                return;
            }
            IntervalEntity intervalEntity = endTimeMap.get(interval.getEndTime());
            if (intervalEntity == null) {
                intervalEntityToSave.add(interval);
            }
        });

        // Create response list by merging old entities from repository and new saved entities
        List<IntervalEntity> response = new ArrayList<>(intervalStringList.size());
        response.addAll(intervalRepository.saveAll(intervalEntityToSave));
        response.addAll(
                intervalEntitiesFromRepository.values().stream()
                        .flatMap(endTimeToEntityMap -> endTimeToEntityMap.values().stream())
                        .toList()
        );
        return response;
    }

    private IntervalEntity createIntervalFromString(String intervalString) {
        int timeDelimiter = intervalString.indexOf('-');
        IntervalEntity interval = new IntervalEntity();
        try {
            String startTimeString = intervalString.substring(0, timeDelimiter);
            interval.setStartTime(dateTimeParser.parseShortTime(startTimeString));

            String endTimeString = intervalString.substring(timeDelimiter + 1);
            interval.setEndTime(dateTimeParser.parseShortTime(endTimeString));
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't parse time " + intervalString);
        }
        return interval;
    }

    public String mapIntervalString(IntervalEntity intervalEntity) {
        return createStringFormInterval(intervalEntity);
    }

    public List<String> mapIntervalStringList(List<IntervalEntity> intervalEntityList) {
        return intervalEntityList.stream().map(this::mapIntervalString).toList();
    }

    private String createStringFormInterval(IntervalEntity intervalEntity) {
        String startTimeString = dateTimeParser.shortTimeToString(intervalEntity.getStartTime());
        String endTimeString = dateTimeParser.shortTimeToString(intervalEntity.getEndTime());
        return startTimeString + '-' + endTimeString;
    }

}
