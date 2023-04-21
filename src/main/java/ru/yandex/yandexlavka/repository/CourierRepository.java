package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;

import java.util.Collection;
import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<CourierEntity, Long> {
    @Transactional(readOnly = true)
    List<CourierEntity> findAllByCourierIdIn(Collection<Long> idList);
}
