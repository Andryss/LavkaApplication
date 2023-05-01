package ru.yandex.yandexlavka.objects.utils.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.objects.entity.RegionEntity;
import ru.yandex.yandexlavka.repository.RegionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class RegionMapperImpl implements RegionMapper {

    private final RegionRepository regionRepository;

    @Autowired
    public RegionMapperImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    @Transactional
    public RegionEntity mapRegionEntity(Integer regionNumber) {
        Optional<RegionEntity> entityFromRepository = regionRepository.findByRegionNumber(regionNumber);
        return entityFromRepository.orElseGet(() -> regionRepository.save(createRegionFromRegionNumber(regionNumber)));
    }

    @Override
    @Transactional
    public List<RegionEntity> mapRegionEntityList(List<Integer> regionNumberList) {
        // Fetch region entities with region number in given list (and transform into region to entity map)
        Map<Integer, RegionEntity> regionEntitiesFromRepository = regionRepository.findAllByRegionNumberIn(regionNumberList).stream()
                .collect(toMap(RegionEntity::getRegionNumber, identity()));

        // Find region numbers, which wasn't found in repository and create list with region entities to save
        List<RegionEntity> regionEntitiesToSave = new ArrayList<>(regionNumberList.size());
        regionNumberList.forEach(region -> {
            if (!regionEntitiesFromRepository.containsKey(region)) {
                regionEntitiesToSave.add(createRegionFromRegionNumber(region));
            }
        });


        List<RegionEntity> response = new ArrayList<>(regionNumberList.size());
        response.addAll(regionRepository.saveAll(regionEntitiesToSave));
        response.addAll(regionEntitiesFromRepository.values());
        return response;
    }

    private RegionEntity createRegionFromRegionNumber(Integer regionNumber){
        return new RegionEntity(
                null,
                regionNumber
        );
    }

    @Override
    public Integer mapRegionNumber(RegionEntity regionEntity) {
        return createRegionNumberFromRegion(regionEntity);
    }

    @Override
    public List<Integer> mapRegionNumberList(List<RegionEntity> regionEntityList) {
        return regionEntityList.stream().map(this::mapRegionNumber).toList();
    }

    private Integer createRegionNumberFromRegion(RegionEntity regionEntity){
        return regionEntity.getRegionNumber();
    }
}
