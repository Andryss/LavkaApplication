package ru.yandex.yandexlavka.entities.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.entities.IntervalEntity;
import ru.yandex.yandexlavka.repositories.IntervalRepository;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class IntervalMapper {

    private final IntervalRepository intervalRepository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    public IntervalMapper(IntervalRepository intervalRepository) {
        this.intervalRepository = intervalRepository;
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
            LocalTime startTimeParsed = LocalTime.parse(startTimeString, timeFormatter);
            interval.setStartTime(Time.valueOf(startTimeParsed));

            String endTimeString = intervalString.substring(timeDelimiter + 1);
            LocalTime endTimeParsed = LocalTime.parse(endTimeString, timeFormatter);
            interval.setEndTime(Time.valueOf(endTimeParsed));
        } catch (Exception e) {
            e.printStackTrace(); // TODO: delete print stack trace
            throw new IllegalArgumentException("Can't parse time " + intervalString);
        }
        return interval;
    }

    public String mapIntervalString(IntervalEntity intervalEntity) {
        return createStringFormInterval(intervalEntity);
    }

    private String createStringFormInterval(IntervalEntity intervalEntity) {
        String startTimeString = timeFormatter.format(intervalEntity.getStartTime().toLocalTime());
        String endTimeString = timeFormatter.format(intervalEntity.getEndTime().toLocalTime());
        return startTimeString + '-' + endTimeString;
    }

}
