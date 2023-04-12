package ru.yandex.yandexlavka.entities.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.entities.IntervalEntity;
import ru.yandex.yandexlavka.repositories.IntervalRepository;
import ru.yandex.yandexlavka.util.DateTimeParser;

import java.util.Optional;

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
        return intervalFromRepository.orElse(intervalFromString);
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

    private String createStringFormInterval(IntervalEntity intervalEntity) {
        String startTimeString = dateTimeParser.shortTimeToString(intervalEntity.getStartTime());
        String endTimeString = dateTimeParser.shortTimeToString(intervalEntity.getEndTime());
        return startTimeString + '-' + endTimeString;
    }

}
