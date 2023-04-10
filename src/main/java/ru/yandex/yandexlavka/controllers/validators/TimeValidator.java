package ru.yandex.yandexlavka.controllers.validators;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeValidator implements Validator {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

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
            int delimiter = timeInterval.indexOf('-');
            String startTimeString = timeInterval.substring(0, delimiter);
            LocalTime startTime = LocalTime.parse(startTimeString, formatter);

            String endTimeString = timeInterval.substring(delimiter + 1);
            LocalTime endTime = LocalTime.parse(endTimeString, formatter);

            if (startTime.isAfter(endTime)) {
                errors.reject("", "End must be after start");
            }
        } catch (Exception e) {
            errors.reject("", "Wrong time format");
        }
    }
}
