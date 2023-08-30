package ru.andryss.lavka.serivce.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.andryss.lavka.objects.dto.CourierType;
import ru.andryss.lavka.objects.entity.CourierEntity;
import ru.andryss.lavka.objects.entity.GroupOrdersEntity;
import ru.andryss.lavka.objects.entity.IntervalEntity;
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

import static java.util.Comparator.comparingDouble;

@Component
@Primary
public class UniformPriorityOrderAssignService extends UniformOrderAssignService {

    @Autowired
    protected UniformPriorityOrderAssignService(CourierRepository courierRepository, OrderRepository orderRepository, GroupOrderRepository groupOrderRepository, GroupOrdersMapper groupOrdersMapper, MaxOrdersAmountResolver maxOrdersAmountResolver, MaxOrdersWeightResolver maxOrdersWeightResolver, MaxOrdersRegionsAmountResolver maxOrdersRegionsAmountResolver) {
        super(courierRepository, orderRepository, groupOrderRepository, groupOrdersMapper, maxOrdersAmountResolver, maxOrdersWeightResolver, maxOrdersRegionsAmountResolver);
    }

    @Override
    protected Map<CourierEntity, List<GroupOrdersEntity>> distributeOrders(LocalDate date, List<CourierEntity> allCourierEntities, Set<OrderEntity> notAssignedOrderEntities) {
        sortCouriers(allCourierEntities);
        return super.distributeOrders(date, allCourierEntities, notAssignedOrderEntities);
    }

    private void sortCouriers(List<CourierEntity> allCourierEntities) {
        Map<CourierEntity, Double> courierPriorities = new HashMap<>();
        allCourierEntities.forEach(courierEntity ->
                courierPriorities.put(courierEntity, getCourierPriority(courierEntity)));

        allCourierEntities.sort(comparingDouble(courierPriorities::get));
    }

    private double getCourierPriority(CourierEntity courierEntity) {
        return getCourierTypePriority(courierEntity) +
                getCourierWorkingHoursPriority(courierEntity);
    }

    private double getCourierTypePriority(CourierEntity courierEntity) {
        return switch (courierEntity.getCourierType()){
            case FOOT -> 1.0;
            case AUTO -> 2.0;
            case BIKE -> 3.0;
        };
    }

    private double getCourierWorkingHoursPriority(CourierEntity courierEntity) {
        double timeCoefficient = 0.0;
        for (IntervalEntity workingHour : courierEntity.getWorkingHours()) {
            timeCoefficient += workingHour.getEndTime().toSecondOfDay() - workingHour.getStartTime().toSecondOfDay();
        }
        return timeCoefficient / 3600;
    }
}
