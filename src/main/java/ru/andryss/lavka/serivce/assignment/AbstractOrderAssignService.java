package ru.andryss.lavka.serivce.assignment;

import org.springframework.transaction.annotation.Transactional;
import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.entity.GroupOrdersEntity;
import ru.andryss.lavka.objects.entity.OrderEntity;
import ru.andryss.lavka.objects.mapping.assign.order.CouriersGroupOrders;
import ru.andryss.lavka.objects.utils.mapper.GroupOrdersMapper;
import ru.andryss.lavka.repository.CourierRepository;
import ru.andryss.lavka.repository.GroupOrderRepository;
import ru.andryss.lavka.repository.OrderRepository;

import java.time.LocalDate;
import java.util.*;

public abstract class AbstractOrderAssignService implements OrderAssignService {

    protected final CourierRepository courierRepository;
    protected final OrderRepository orderRepository;
    protected final GroupOrderRepository groupOrderRepository;

    protected final GroupOrdersMapper groupOrdersMapper;

    protected AbstractOrderAssignService(CourierRepository courierRepository, OrderRepository orderRepository, GroupOrderRepository groupOrderRepository, GroupOrdersMapper groupOrdersMapper) {
        this.courierRepository = courierRepository;
        this.orderRepository = orderRepository;
        this.groupOrderRepository = groupOrderRepository;
        this.groupOrdersMapper = groupOrdersMapper;
    }

    @Override
    @Transactional
    public AssignedOrdersInfo assignOrders(LocalDate date) {
        // Get available couriers (all?) and not assigned orders
        List<CourierEntity> allCourierEntities = courierRepository.findAll();
        Set<OrderEntity> notAssignedOrderEntities = orderRepository.findAllByAssignedGroupOrderNull();

        // Distribute orders between couriers
        Map<CourierEntity, List<GroupOrdersEntity>> assignedGroupOrders = distributeOrders(date, allCourierEntities, notAssignedOrderEntities);

        // Save groups to get ids
        Map<CourierEntity, List<GroupOrdersEntity>> assignedSavedGroupOrders = saveAssignedGroupOrders(assignedGroupOrders);

        // Form response
        return createResponse(assignedSavedGroupOrders);
    }

    protected abstract Map<CourierEntity, List<GroupOrdersEntity>> distributeOrders(
            LocalDate date,
            List<CourierEntity> allCourierEntities,
            Set<OrderEntity> notAssignedOrderEntities
    );

    private Map<CourierEntity, List<GroupOrdersEntity>> saveAssignedGroupOrders(Map<CourierEntity, List<GroupOrdersEntity>> assignedGroupOrders) {
        Map<CourierEntity, List<GroupOrdersEntity>> assignedSavedGroupOrders = new HashMap<>();

        assignedGroupOrders.forEach((courierEntity, groupOrdersEntityList) -> {
            List<GroupOrdersEntity> savedGroupOrders = groupOrderRepository.saveAll(groupOrdersEntityList);
            savedGroupOrders.forEach(groupOrders -> {
                groupOrders.getAssignedCourier().getAssignedGroupOrders().add(groupOrders);
                groupOrders.getOrders().forEach(orderEntity -> orderEntity.setAssignedGroupOrder(groupOrders));
            });
            assignedSavedGroupOrders.put(courierEntity, savedGroupOrders);
        });

        return assignedSavedGroupOrders;
    }

    private AssignedOrdersInfo createResponse(Map<CourierEntity, List<GroupOrdersEntity>> assignedSavedGroupOrders) {
        List<CouriersGroupOrders> couriersGroupOrdersList = new ArrayList<>();

        assignedSavedGroupOrders.forEach(
                ((courierEntity, groupOrdersEntityList) ->
                        couriersGroupOrdersList.add(new CouriersGroupOrders(
                                courierEntity.getCourierId(),
                                groupOrdersMapper.mapGroupOrdersList(groupOrdersEntityList))
                        )));

        return new AssignedOrdersInfo(couriersGroupOrdersList);
    }
}
