package ru.andryss.lavka.objects.mapping.get.courier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.andryss.lavka.objects.dto.CourierDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCourierResponse {
    CourierDto courier;
}
