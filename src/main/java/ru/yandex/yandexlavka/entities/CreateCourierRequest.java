package ru.yandex.yandexlavka.entities;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.yandexlavka.entities.dto.CreateCourierDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourierRequest {
    @NotNull
    List<CreateCourierDto> couriers;
}
