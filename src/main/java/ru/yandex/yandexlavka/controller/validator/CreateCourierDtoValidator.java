package ru.yandex.yandexlavka.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.yandex.yandexlavka.objects.mapping.create.courier.CreateCourierDto;

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
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        CreateCourierDto createCourierDto = ((CreateCourierDto) target);
        if (createCourierDto.getWorkingHours() == null) {
            errors.reject("", "Target is null");
            return;
        }
        createCourierDto.getWorkingHours().forEach(s -> {
            if (!errors.hasErrors())
                timeValidator.validate(s, errors);
        });
    }
}
