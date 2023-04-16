package ru.yandex.yandexlavka.entities.couriers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourierRequest {
    @NotEmpty
    List<@Valid CreateCourierDto> couriers;
}
