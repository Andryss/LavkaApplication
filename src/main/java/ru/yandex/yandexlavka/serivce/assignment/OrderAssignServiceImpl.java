package ru.yandex.yandexlavka.serivce.assignment;

import org.springframework.stereotype.Component;
import ru.yandex.yandexlavka.objects.mapping.assign.order.OrderAssignResponse;

import java.time.LocalDate;
import java.util.List;

@Component
public class OrderAssignServiceImpl implements OrderAssignService {

    @Override
    public List<OrderAssignResponse> assignOrders(LocalDate date) {
        // TODO: implement
        return null;
    }
}