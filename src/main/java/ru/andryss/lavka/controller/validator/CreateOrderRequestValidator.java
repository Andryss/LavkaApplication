package ru.andryss.lavka.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.andryss.lavka.objects.mapping.create.courier.CreateCourierRequest;
import ru.andryss.lavka.objects.mapping.create.order.CreateOrderRequest;

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
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        CreateOrderRequest createOrderRequest = (CreateOrderRequest) target;
        if (createOrderRequest.getOrders() == null) {
            errors.reject("", "Target is null");
            return;
        }
        createOrderRequest.getOrders().forEach(createCourierDto -> {
            if (!errors.hasErrors())
                createOrderDtoValidator.validate(createCourierDto, errors);
        });
    }
}
