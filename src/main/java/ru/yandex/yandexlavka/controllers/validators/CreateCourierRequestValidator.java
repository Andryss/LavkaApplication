package ru.yandex.yandexlavka.controllers.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.yandex.yandexlavka.entities.couriers.CreateCourierRequest;

@Component
public class CreateCourierRequestValidator implements Validator {

    private final CreateCourierDtoValidator createCourierDtoValidator;

    @Autowired
    public CreateCourierRequestValidator(CreateCourierDtoValidator createCourierDtoValidator) {
        this.createCourierDtoValidator = createCourierDtoValidator;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return CreateCourierRequest.class.equals(clazz);
    }

    @Override
    public void validate(@Nullable Object target, @NonNull Errors errors) {
        CreateCourierRequest createCourierRequest = (CreateCourierRequest) target;
        if (createCourierRequest == null || createCourierRequest.getCouriers() == null) {
            errors.reject("", "Target is null");
            return;
        }
        createCourierRequest.getCouriers().forEach(createCourierDto -> createCourierDtoValidator.validate(createCourierDto, errors));
    }
}
