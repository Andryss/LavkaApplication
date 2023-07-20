package ru.yandex.yandexlavka.objects.mapping.create.courier;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
    List<@NotNull @Positive Integer> regions;

    @NotEmpty
    List<@NotNull @Pattern(regexp = "^([0-1]\\d|2[0-3]):[0-5]\\d-([0-1]\\d|2[0-3]):[0-5]\\d$") String> workingHours;
}
