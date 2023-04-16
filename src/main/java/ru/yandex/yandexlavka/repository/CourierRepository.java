package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;

import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<CourierEntity, Long> {
    List<CourierEntity> findAllByCourierIdIn(List<Long> idList);
}
