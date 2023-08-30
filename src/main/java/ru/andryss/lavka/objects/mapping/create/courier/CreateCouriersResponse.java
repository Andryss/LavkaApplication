package ru.andryss.lavka.objects.mapping.create.courier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.andryss.lavka.objects.dto.CourierDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCouriersResponse {
    List<CourierDto> couriers;
}
