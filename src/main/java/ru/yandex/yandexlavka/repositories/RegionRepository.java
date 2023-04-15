package ru.yandex.yandexlavka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entities.RegionEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Long> {
    Optional<RegionEntity> findByRegionNumber(Integer regionNumber);
    Set<RegionEntity> findAllByRegionNumberIn(List<Integer> regionNumberList);
}
