package ru.yandex.yandexlavka.controllers.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.yandex.yandexlavka.entities.couriers.CreateCourierDto;

@Component
public class CreateCourierDtoValidator implements Validator {

    private final TimeValidator timeValidator;

    @Autowired
    public CreateCourierDtoValidator(TimeValidator timeValidator) {
        this.timeValidator = timeValidator;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return CreateCourierDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@Nullable Object target, @NonNull Errors errors) {
        if (target == null) {
            errors.reject("", "Target is null");
            return;
        }
        CreateCourierDto createCourierDto = ((CreateCourierDto) target);
        createCourierDto.getWorkingHours().forEach(s -> timeValidator.validate(s, errors));
    }
}
