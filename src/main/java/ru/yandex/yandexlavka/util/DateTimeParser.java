package ru.yandex.yandexlavka.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeParser {

    private final DateTimeFormatter shortTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    public LocalTime parseShortTime(String timeString) {
        return LocalTime.parse(timeString, shortDateFormatter);
    }
    public String shortTimeToString(LocalTime time) {
        return shortTimeFormatter.format(time);
    }


    private final DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public LocalDate parseShortDate(String dateString) {
        return LocalDate.parse(dateString, shortDateFormatter);
    }
    public String shortDateToString(LocalDate date) {
        return shortDateFormatter.format(date);
    }

}
