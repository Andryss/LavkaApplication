package ru.andryss.lavka.objects.mapping.create.courier;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourierRequest {
    @NotEmpty
    List<@NotNull @Valid CreateCourierDto> couriers;
}
