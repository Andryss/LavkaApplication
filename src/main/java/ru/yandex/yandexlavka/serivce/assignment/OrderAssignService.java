package ru.yandex.yandexlavka.serivce.assignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.objects.mapping.assign.order.CouriersGroupOrders;

import java.time.LocalDate;
import java.util.List;

public interface OrderAssignService {

    AssignedOrdersInfo assignOrders(LocalDate date);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class AssignedOrdersInfo {
        List<CouriersGroupOrders> assignedCouriersGroupOrders;
    }

}
