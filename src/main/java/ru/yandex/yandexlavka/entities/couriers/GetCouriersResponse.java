package ru.yandex.yandexlavka.entities.couriers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCouriersResponse {
    List<CourierDto> couriers;
    Integer limit;
    Integer offset;
}
