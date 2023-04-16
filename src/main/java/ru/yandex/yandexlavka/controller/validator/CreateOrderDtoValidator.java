package ru.yandex.yandexlavka.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.yandex.yandexlavka.objects.mapping.create.order.CreateOrderDto;

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
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        CreateOrderDto createOrderDto = ((CreateOrderDto) target);
        if (createOrderDto.getDeliveryHours() == null) {
            errors.reject("", "Target is null");
            return;
        }
        createOrderDto.getDeliveryHours().forEach(s -> {
            if (!errors.hasErrors())
                timeValidator.validate(s, errors);
        });
    }
}
