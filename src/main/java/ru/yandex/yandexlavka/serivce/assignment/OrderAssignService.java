package ru.yandex.yandexlavka.serivce.assignment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.yandex.yandexlavka.objects.mapping.assign.order.CouriersGroupOrders;

import java.time.LocalDate;
import java.util.List;

public interface OrderAssignService {

    /**
     * Splits orders into groups and assign that groups to couriers
     * @param date assignment date
     * @return order groups assigned to couriers
     * @see ru.yandex.yandexlavka.controller.OrderController
     */
    AssignedOrdersInfo assignOrders(LocalDate date);

    @Getter
    @AllArgsConstructor
    class AssignedOrdersInfo {
        List<CouriersGroupOrders> assignedCouriersGroupOrders;
    }

}
