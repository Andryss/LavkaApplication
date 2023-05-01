package ru.yandex.yandexlavka.objects.utils.mapper;

import ru.yandex.yandexlavka.objects.entity.RegionEntity;

import java.util.List;

public interface RegionMapper {

    RegionEntity mapRegionEntity(Integer regionNumber);
    List<RegionEntity> mapRegionEntityList(List<Integer> regionNumberList);

    Integer mapRegionNumber(RegionEntity regionEntity);
    List<Integer> mapRegionNumberList(List<RegionEntity> regionEntityList);

}
