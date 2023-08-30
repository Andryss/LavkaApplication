package ru.andryss.lavka.objects.utils.mapper;

import ru.andryss.lavka.objects.entity.RegionEntity;

import java.util.List;

public interface RegionMapper {

    RegionEntity mapRegionEntity(Integer regionNumber);
    List<RegionEntity> mapRegionEntityList(List<Integer> regionNumberList);

    Integer mapRegionNumber(RegionEntity regionEntity);
    List<Integer> mapRegionNumberList(List<RegionEntity> regionEntityList);

}
