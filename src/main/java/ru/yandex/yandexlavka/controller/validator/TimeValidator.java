package ru.yandex.yandexlavka.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.yandex.yandexlavka.util.DateTimeParser;

import java.time.LocalTime;

@Component
public class TimeValidator implements Validator {

    private final DateTimeParser dateTimeParser;

    @Autowired
    public TimeValidator(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return String.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        String timeInterval = (String) target;
        int delimiter = timeInterval.indexOf('-');
        if (timeInterval.length() != 11 || delimiter != 5) {
            errors.reject("", "Wrong time format");
            return;
        }

        String startTimeString = timeInterval.substring(0, delimiter);
        String endTimeString = timeInterval.substring(delimiter + 1);
        if (dateTimeParser.isInvalidShortTime(startTimeString) || dateTimeParser.isInvalidShortTime(endTimeString)) {
            errors.reject("", "Wrong time format");
            return;
        }

        try {
            LocalTime startTime = dateTimeParser.parseShortTime(startTimeString);
            LocalTime endTime = dateTimeParser.parseShortTime(endTimeString);

            if (startTime.isAfter(endTime)) {
                errors.reject("", "End must be after start");
            }
        } catch (Exception e) {
            errors.reject("", "Wrong time format");
        }
    }
}
