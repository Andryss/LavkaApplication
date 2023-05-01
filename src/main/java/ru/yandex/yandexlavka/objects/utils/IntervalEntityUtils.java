package ru.yandex.yandexlavka.objects.utils;

import ru.yandex.yandexlavka.objects.entity.IntervalEntity;

import java.util.List;

import static java.util.Comparator.comparing;

public class IntervalEntityUtils {

    public static boolean isIntersecting(IntervalEntity interval1, IntervalEntity interval2) {
        return !interval1.getStartTime().isBefore(interval2.getStartTime()) && !interval1.getStartTime().isAfter(interval2.getEndTime()) ||
                !interval1.getEndTime().isBefore(interval2.getStartTime()) && !interval1.getEndTime().isAfter(interval2.getEndTime());
    }

    public static boolean hasIntersections(List<IntervalEntity> intervalEntityList) {
        if (intervalEntityList.size() == 1) return false;
        intervalEntityList.sort(comparing(IntervalEntity::getStartTime));
        for (int i = 0; i < intervalEntityList.size() - 1; i++) {
            if (isIntersecting(intervalEntityList.get(i), intervalEntityList.get(i + 1)))
                return true;
        }
        return false;
    }
}
