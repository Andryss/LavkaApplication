package ru.yandex.yandexlavka.objects.mapping.create.courier;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.objects.dto.CourierType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourierDto {
    @NotNull
    CourierType courierType;

    @NotEmpty
    List<Integer> regions;

    @NotEmpty
    List<String> workingHours;
}
