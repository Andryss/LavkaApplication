package ru.yandex.yandexlavka.util.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class IntervalGenerator {

    public String mapDoubleToTimeString(double part) {
        int minutesOfDay = (int) (part * (24 * 60));
        int hours = minutesOfDay / 60;
        int minutes = minutesOfDay % 60;
        return "" + (hours / 10) + (hours % 10) + ":" + (minutes / 10) + (minutes % 10);
    }

    public List<String> createIntervals() {
        int pointsCount = (int) (Math.random() * 20 + 2);

        List<String> timeList = new Random().doubles(pointsCount)
                .sorted()
                .mapToObj(this::mapDoubleToTimeString)
                .toList();

        List<String> intervals = new ArrayList<>(pointsCount / 2);

        String firstPart = null;
        for (String time : timeList) {
            if (firstPart == null) {
                firstPart = time;
            } else {
                intervals.add(firstPart + "-" + time);
                firstPart = null;
            }
        }

        return intervals;
    }

}
