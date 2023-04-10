package ru.yandex.yandexlavka.controllers.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.yandex.yandexlavka.entities.orders.CreateOrderDto;

@Component
public class CreateOrderDtoValidator implements Validator {

    private final TimeValidator timeValidator;

    @Autowired
    public CreateOrderDtoValidator(TimeValidator timeValidator) {
        this.timeValidator = timeValidator;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return CreateOrderDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@Nullable Object target, @NonNull Errors errors) {
        if (target == null) {
            errors.reject("", "Target is null");
            return;
        }
        CreateOrderDto createOrderDto = ((CreateOrderDto) target);
        createOrderDto.getDeliveryHours().forEach(s -> timeValidator.validate(s, errors));
    }
}
