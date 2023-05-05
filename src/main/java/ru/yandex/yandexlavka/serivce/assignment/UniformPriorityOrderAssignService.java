package ru.yandex.yandexlavka.serivce.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.entity.CourierEntity;
import ru.yandex.yandexlavka.objects.entity.GroupOrdersEntity;
import ru.yandex.yandexlavka.objects.entity.IntervalEntity;
import ru.yandex.yandexlavka.objects.entity.OrderEntity;
import ru.yandex.yandexlavka.objects.utils.mapper.GroupOrdersMapper;
import ru.yandex.yandexlavka.repository.CourierRepository;
import ru.yandex.yandexlavka.repository.GroupOrderRepository;
import ru.yandex.yandexlavka.repository.OrderRepository;
import ru.yandex.yandexlavka.serivce.assignment.util.MaxOrdersAmountResolver;
import ru.yandex.yandexlavka.serivce.assignment.util.MaxOrdersRegionsAmountResolver;
import ru.yandex.yandexlavka.serivce.assignment.util.MaxOrdersWeightResolver;

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
