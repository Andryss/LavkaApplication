package ru.yandex.yandexlavka.objects.mapping.get.courier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.objects.dto.CourierDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCourierResponse {
    CourierDto courier;
}
