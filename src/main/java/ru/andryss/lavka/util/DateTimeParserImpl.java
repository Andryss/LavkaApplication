package ru.andryss.lavka.util;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeParserImpl implements DateTimeParser {

    private final DateTimeFormatter shortTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    public boolean isInvalidShortTime(String time) {
        if (time.length() != 5) return true;
        char[] chars = time.toCharArray();
        return chars[0] < '0' || chars[0] > '2' ||
                !Character.isDigit(chars[1]) ||
                chars[2] != ':' ||
                chars[3] < '0' || chars[3] > '5' ||
                !Character.isDigit(chars[4]);
    }
    public LocalTime parseShortTime(String timeString) {
        return LocalTime.parse(timeString, shortTimeFormatter);
    }
    public String shortTimeToString(LocalTime time) {
        return shortTimeFormatter.format(time);
    }

}
