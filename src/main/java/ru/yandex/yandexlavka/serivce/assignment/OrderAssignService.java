package ru.yandex.yandexlavka.serivce.assignment;

import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;

import java.time.LocalDate;
import java.util.List;

public interface OrderAssignService {

    List<OrderAssignResponse> assignOrders(LocalDate date);

}
