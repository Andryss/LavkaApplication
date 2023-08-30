package ru.andryss.lavka.objects.mapping.get.courier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.andryss.lavka.objects.dto.CourierDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCouriersResponse {
    List<CourierDto> couriers;
    Integer limit;
    Integer offset;
}
