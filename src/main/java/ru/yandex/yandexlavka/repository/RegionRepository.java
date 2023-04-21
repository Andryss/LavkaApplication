package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.objects.entity.RegionEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Long> {
    @Transactional(readOnly = true)
    Optional<RegionEntity> findByRegionNumber(Integer regionNumber);

    @Transactional(readOnly = true)
    Set<RegionEntity> findAllByRegionNumberIn(Collection<Integer> regionNumberList);
}
