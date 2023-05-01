package ru.yandex.yandexlavka.objects.mapping.assign.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAssignResponse {
    String date;
    List<CouriersGroupOrders> couriers;
}
