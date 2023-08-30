package ru.andryss.lavka.objects.utils.mapper;

import ru.andryss.lavka.objects.entity.IntervalEntity;

import java.util.List;

public interface IntervalMapper {

    IntervalEntity mapIntervalEntity(String intervalString);
    List<IntervalEntity> mapIntervalEntityList(List<String> intervalStringList);

    String mapIntervalString(IntervalEntity intervalEntity);
    List<String> mapIntervalStringList(List<IntervalEntity> intervalEntityList);
}
