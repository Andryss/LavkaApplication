package ru.andryss.lavka.objects.utils;

import ru.andryss.lavka.objects.entity.IntervalEntity;

import java.time.LocalTime;
import java.util.List;

import static java.util.Comparator.comparing;

public class IntervalEntityUtils {

    public static boolean belongsInterval(LocalTime point, IntervalEntity interval) {
        return !point.isBefore(interval.getStartTime()) && !point.isAfter(interval.getEndTime());
    }

    public static boolean isIntersecting(IntervalEntity interval1, IntervalEntity interval2) {
        return belongsInterval(interval1.getStartTime(), interval2) || belongsInterval(interval2.getStartTime(), interval1);
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

    public static boolean hasIntersectionsBetween(List<IntervalEntity> intervalList1, List<IntervalEntity> intervalList2) {
        intervalList1.sort(comparing(IntervalEntity::getStartTime));
        intervalList2.sort(comparing(IntervalEntity::getStartTime));
        int i1 = 0, i2 = 0;
        while (i1 < intervalList1.size() && i2 < intervalList2.size()) {
            IntervalEntity interval1 = intervalList1.get(i1), interval2 = intervalList2.get(i2);
            if (isIntersecting(interval1, interval2)) return true;
            if (interval1.getStartTime().compareTo(interval2.getStartTime()) > 0) i2++;
            else i1++;
        }
        return false;
    }

    public static boolean isInsideInterval(LocalTime time, IntervalEntity interval) {
        return !time.isBefore(interval.getStartTime()) && !time.isAfter(interval.getEndTime());
    }

    public static boolean isInsideAnyInterval(LocalTime time, List<IntervalEntity> intervalList) {
        return intervalList.stream().anyMatch(interval -> isInsideInterval(time, interval));
    }

    public static LocalTime getIntersectionPoint(IntervalEntity interval1, IntervalEntity interval2) {
        if (!interval1.getStartTime().isBefore(interval2.getStartTime()) && !interval1.getStartTime().isAfter(interval2.getEndTime()))
            return interval1.getStartTime();
        if (!interval1.getEndTime().isBefore(interval2.getStartTime()) && !interval1.getEndTime().isAfter(interval2.getEndTime()))
            return interval1.getEndTime();
        throw new IllegalArgumentException("No intersection");
    }

    public static LocalTime getIntersectionPointBetween(List<IntervalEntity> intervalList1, List<IntervalEntity> intervalList2) {
        intervalList1.sort(comparing(IntervalEntity::getStartTime));
        intervalList2.sort(comparing(IntervalEntity::getStartTime));
        int i1 = 0, i2 = 0;
        while (i1 < intervalList1.size() && i2 < intervalList2.size()) {
            IntervalEntity interval1 = intervalList1.get(i1), interval2 = intervalList2.get(i2);
            if (isIntersecting(interval1, interval2)) return getIntersectionPoint(interval1, interval2);
            if (interval1.getStartTime().compareTo(interval2.getStartTime()) > 0) i2++;
            else i1++;
        }
        throw new IllegalArgumentException("No intersection");
    }
}
