package ru.andryss.lavka.objects.mapping.assign.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAssignResponse {
    String date;
    List<CouriersGroupOrders> couriers;
}
