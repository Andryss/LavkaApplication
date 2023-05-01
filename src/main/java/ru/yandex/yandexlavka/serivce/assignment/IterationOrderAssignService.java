package ru.yandex.yandexlavka.serivce.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.exception.BadRequestException;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.GroupOrdersEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;
import ru.yandex.yandexlavka.objects.mapping.assign.order.CouriersGroupOrders;
import ru.yandex.yandexlavka.objects.utils.mapper.GroupOrdersMapper;
import ru.yandex.yandexlavka.repository.CourierRepository;
import ru.yandex.yandexlavka.repository.GroupOrderRepository;
import ru.yandex.yandexlavka.repository.OrderRepository;
import ru.yandex.yandexlavka.serivce.assignment.util.MaxOrdersAmountResolver;
import ru.yandex.yandexlavka.serivce.assignment.util.MaxOrdersRegionsAmountResolver;
import ru.yandex.yandexlavka.serivce.assignment.util.MaxOrdersWeightResolver;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.yandexlavka.objects.utils.IntervalEntityUtils.hasIntersectionsBetween;

@Component
public class IterationOrderAssignService implements OrderAssignService {

    private final CourierRepository courierRepository;
    private final GroupOrderRepository groupOrderRepository;
    private final OrderRepository orderRepository;

    private final GroupOrdersMapper groupOrdersMapper;

    private final MaxOrdersAmountResolver maxOrdersAmountResolver;
    private final MaxOrdersWeightResolver maxOrdersWeightResolver;
    private final MaxOrdersRegionsAmountResolver maxOrdersRegionsAmountResolver;

    @Autowired
    public IterationOrderAssignService(CourierRepository courierRepository, GroupOrderRepository groupOrderRepository, OrderRepository orderRepository, GroupOrdersMapper groupOrdersMapper, MaxOrdersAmountResolver maxOrdersAmountResolver, MaxOrdersWeightResolver maxOrdersWeightResolver, MaxOrdersRegionsAmountResolver maxOrdersRegionsAmountResolver) {
        this.courierRepository = courierRepository;
        this.groupOrderRepository = groupOrderRepository;
        this.orderRepository = orderRepository;
        this.groupOrdersMapper = groupOrdersMapper;
        this.maxOrdersAmountResolver = maxOrdersAmountResolver;
        this.maxOrdersWeightResolver = maxOrdersWeightResolver;
        this.maxOrdersRegionsAmountResolver = maxOrdersRegionsAmountResolver;
    }

    @Override
    @Transactional
    public AssignedOrdersInfo assignOrders(LocalDate date) {
        // Check if nothing has already assigned at this date
        if (groupOrderRepository.existsByAssignedDateEquals(date))
            throw BadRequestException.EMPTY;

        // Get available couriers (all?) and not assigned orders
        List<CourierEntity> allCourierEntities = courierRepository.findAll();
        Set<OrderEntity> notAssignedOrderEntities = orderRepository.findAllByAssignedGroupOrderNull();

        // Create Map<Courier,AssignedGroups>
        Map<CourierEntity, List<GroupOrdersEntity>> assignedGroupOrders = new HashMap<>();

        // Iteration
        while (!notAssignedOrderEntities.isEmpty()) {
            boolean isSomethingAssigned = false;
            for (CourierEntity courierEntity : allCourierEntities) {
                // Get orders which can deliver that courier
                Set<OrderEntity> possibleOrders = getPossibleOrders(courierEntity, notAssignedOrderEntities);
                if (possibleOrders.isEmpty()) continue;

                // Create some group from possible orders
                GroupOrdersEntity groupOrdersEntity = createGroupOrders(date, courierEntity, possibleOrders);
                assignedGroupOrders.computeIfAbsent(courierEntity, c -> new ArrayList<>()).add(groupOrdersEntity);

                // Remove assigned orders
                groupOrdersEntity.getOrders().forEach(notAssignedOrderEntities::remove);
                isSomethingAssigned = true;
            }
            if (!isSomethingAssigned) break;
        }

        // Save groups to get ids
        Map<CourierEntity, List<GroupOrdersEntity>> assignedSavedGroupOrders = new HashMap<>();
        assignedGroupOrders.forEach((((courierEntity, groupOrdersEntityList) ->
                assignedSavedGroupOrders.put(courierEntity, groupOrderRepository.saveAll(groupOrdersEntityList)))));

        // Form response
        List<CouriersGroupOrders> couriersGroupOrdersList = new ArrayList<>();

        assignedSavedGroupOrders.forEach(
                ((courierEntity, groupOrdersEntityList) ->
                        couriersGroupOrdersList.add(new CouriersGroupOrders(
                                courierEntity.getCourierId(),
                                groupOrdersMapper.mapGroupOrdersList(groupOrdersEntityList))
                        )));

        return new AssignedOrdersInfo(
                couriersGroupOrdersList
        );
    }

    private Set<OrderEntity> getPossibleOrders(CourierEntity courierEntity, Set<OrderEntity> notAssignedOrderEntities) {
        return notAssignedOrderEntities.stream()
                .filter(orderEntity -> hasIntersectionsBetween(courierEntity.getWorkingHours(), orderEntity.getDeliveryHours()))
                .collect(Collectors.toSet());
    }

    private GroupOrdersEntity createGroupOrders(LocalDate date, CourierEntity courierEntity, Set<OrderEntity> possibleOrders) {
        // Resolve max constraints
        int maxOrderAmount = maxOrdersAmountResolver.resolve(courierEntity);
        int maxRegionsAmount = maxOrdersRegionsAmountResolver.resolve(courierEntity);
        float maxOrderWeight = maxOrdersWeightResolver.resolve(courierEntity);

        // Create group orders
        List<OrderEntity> groupOrders = new ArrayList<>();
        Set<Integer> regions = new TreeSet<>();
        float ordersWeight = 0;

        // Iterate through possible orders
        for (OrderEntity orderEntity : possibleOrders) {
            // If regions amount is max
            if (regions.size() == maxRegionsAmount && !regions.contains(orderEntity.getRegions().getRegionNumber())) continue;
            // If weight is too big
            if (orderEntity.getWeight() > maxOrderWeight - ordersWeight) continue;

            // Add order to group
            groupOrders.add(orderEntity);
            if (groupOrders.size() >= maxOrderAmount) break;
            regions.add(orderEntity.getRegions().getRegionNumber());
            ordersWeight += orderEntity.getWeight();
        }

        return new GroupOrdersEntity(null, date, courierEntity, groupOrders);
    }
}
