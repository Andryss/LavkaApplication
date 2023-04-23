package ru.yandex.yandexlavka.util;

import java.time.LocalTime;

public interface DateTimeParser {

    boolean isInvalidShortTime(String time);

    LocalTime parseShortTime(String timeString);

    String shortTimeToString(LocalTime time);
}
