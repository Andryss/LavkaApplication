package ru.yandex.yandexlavka.controllers.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.yandex.yandexlavka.entities.couriers.CreateCourierRequest;
import ru.yandex.yandexlavka.entities.orders.CreateOrderRequest;

@Component
public class CreateOrderRequestValidator implements Validator {

    private final CreateOrderDtoValidator createOrderDtoValidator;

    @Autowired
    public CreateOrderRequestValidator(CreateOrderDtoValidator createOrderDtoValidator) {
        this.createOrderDtoValidator = createOrderDtoValidator;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return CreateCourierRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@Nullable Object target, @NonNull Errors errors) {
        CreateOrderRequest createOrderRequest = (CreateOrderRequest) target;
        if (createOrderRequest == null || createOrderRequest.getOrders() == null) {
            errors.reject("", "Target is null");
            return;
        }
        createOrderRequest.getOrders().forEach(createCourierDto -> createOrderDtoValidator.validate(createCourierDto, errors));
    }
}
