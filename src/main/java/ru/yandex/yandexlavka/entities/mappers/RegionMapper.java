package ru.yandex.yandexlavka.entities.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.entities.RegionEntity;
import ru.yandex.yandexlavka.repositories.RegionRepository;

import java.util.Optional;

@Component
public class RegionMapper {

    private final RegionRepository regionRepository;

    @Autowired
    public RegionMapper(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public RegionEntity mapRegionEntity(Integer regionNumber){
        Optional<RegionEntity> entityFromRepository = regionRepository.findByRegionNumber(regionNumber);
        return entityFromRepository.orElse(createRegionFromRegionNumber(regionNumber));
    }

    private RegionEntity createRegionFromRegionNumber(Integer regionNumber){
        return new RegionEntity(
                null,
                regionNumber
        );
    }

    public Integer mapRegionNumber(RegionEntity regionEntity) {
        return createRegionNumberFromRegion(regionEntity);
    }

    private Integer createRegionNumberFromRegion(RegionEntity regionEntity){
        return regionEntity.getRegionNumber();
    }
}
