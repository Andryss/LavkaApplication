package ru.andryss.lavka.serivce.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.entity.GroupOrdersEntity;
import ru.andryss.lavka.objects.entity.OrderEntity;
import ru.andryss.lavka.objects.utils.mapper.GroupOrdersMapper;
import ru.andryss.lavka.repository.CourierRepository;
import ru.andryss.lavka.repository.GroupOrderRepository;
import ru.andryss.lavka.repository.OrderRepository;
import ru.andryss.lavka.serivce.assignment.util.MaxOrdersAmountResolver;
import ru.andryss.lavka.serivce.assignment.util.MaxOrdersRegionsAmountResolver;
import ru.andryss.lavka.serivce.assignment.util.MaxOrdersWeightResolver;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.andryss.lavka.objects.utils.IntervalEntityUtils.hasIntersectionsBetween;

@Component
public class UniformOrderAssignService extends AbstractOrderAssignService {

    private final MaxOrdersAmountResolver maxOrdersAmountResolver;
    private final MaxOrdersWeightResolver maxOrdersWeightResolver;
    private final MaxOrdersRegionsAmountResolver maxOrdersRegionsAmountResolver;

    @Autowired
    protected UniformOrderAssignService(CourierRepository courierRepository, OrderRepository orderRepository, GroupOrderRepository groupOrderRepository, GroupOrdersMapper groupOrdersMapper, MaxOrdersAmountResolver maxOrdersAmountResolver, MaxOrdersWeightResolver maxOrdersWeightResolver, MaxOrdersRegionsAmountResolver maxOrdersRegionsAmountResolver) {
        super(courierRepository, orderRepository, groupOrderRepository, groupOrdersMapper);
        this.maxOrdersAmountResolver = maxOrdersAmountResolver;
        this.maxOrdersWeightResolver = maxOrdersWeightResolver;
        this.maxOrdersRegionsAmountResolver = maxOrdersRegionsAmountResolver;
    }

    @Override
    protected Map<CourierEntity, List<GroupOrdersEntity>> distributeOrders(LocalDate date, List<CourierEntity> allCourierEntities, Set<OrderEntity> notAssignedOrderEntities) {
        Map<CourierEntity, List<GroupOrdersEntity>> assignedGroupOrders = new HashMap<>();
        Map<CourierEntity, Set<OrderEntity>> possibleCourierOrders = new HashMap<>();

        // Iteration
        while (!notAssignedOrderEntities.isEmpty()) {
            boolean isSomethingAssigned = false;
            for (CourierEntity courierEntity : allCourierEntities) {
                // Get orders which can deliver that courier
                Set<OrderEntity> possibleOrders = possibleCourierOrders.get(courierEntity);
                if (possibleOrders == null) {
                    possibleOrders = getPossibleOrders(courierEntity, notAssignedOrderEntities);
                    possibleCourierOrders.put(courierEntity, possibleOrders);
                } else {
                    possibleOrders.retainAll(notAssignedOrderEntities);
                }
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

        return assignedGroupOrders;
    }

    private Set<OrderEntity> getPossibleOrders(CourierEntity courierEntity, Set<OrderEntity> notAssignedOrderEntities) {
        float maxOrderWeight = maxOrdersWeightResolver.resolve(courierEntity);
        return notAssignedOrderEntities.stream()
                .filter(orderEntity -> orderEntity.getWeight() <= maxOrderWeight)
                .filter(orderEntity -> courierEntity.getRegions().contains(orderEntity.getRegions()))
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
