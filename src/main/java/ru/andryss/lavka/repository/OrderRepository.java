package ru.andryss.lavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.andryss.lavka.objects.entity.GroupOrdersEntity;
import ru.andryss.lavka.objects.entity.OrderEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Transactional(readOnly = true)
    List<OrderEntity> findAllByOrderIdIn(Collection<Long> idList);

    @Transactional(readOnly = true)
    List<OrderEntity> findAllByAssignedGroupOrderIn(Collection<GroupOrdersEntity> groupOrdersEntities);

    @Transactional(readOnly = true)
    HashSet<OrderEntity> findAllByAssignedGroupOrderNull();
}
