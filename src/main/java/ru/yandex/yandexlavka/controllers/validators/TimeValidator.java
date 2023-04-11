package ru.yandex.yandexlavka.controllers.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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
    public void validate(@Nullable Object target, @NonNull Errors errors) {
        if (target == null) {
            errors.reject("", "Target is null");
            return;
        }

        String timeInterval = (String) target;
        if (timeInterval.length() != 11) {
            errors.reject("", "Wrong time format");
            return;
        }

        try {
            // TODO: add checks to avoid exception throwing
            int delimiter = timeInterval.indexOf('-');
            String startTimeString = timeInterval.substring(0, delimiter);
            LocalTime startTime = dateTimeParser.parseShortTime(startTimeString);

            String endTimeString = timeInterval.substring(delimiter + 1);
            LocalTime endTime = dateTimeParser.parseShortTime(endTimeString);

            if (startTime.isAfter(endTime)) {
                errors.reject("", "End must be after start");
            }
        } catch (Exception e) {
            errors.reject("", "Wrong time format");
        }
    }
}
