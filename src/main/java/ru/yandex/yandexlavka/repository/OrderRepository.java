package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.objects.entity.GroupOrdersEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Transactional(readOnly = true)
    List<OrderEntity> findAllByOrderIdIn(Collection<Long> idList);

    @Transactional(readOnly = true)
    List<OrderEntity> findAllByAssignedGroupOrderIn(Collection<GroupOrdersEntity> groupOrdersEntities);
}
