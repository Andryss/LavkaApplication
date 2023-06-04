package ru.yandex.yandexlavka.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.yandexlavka.exception.BadRequestException;
import ru.yandex.yandexlavka.exception.BadRequestResponse;
import ru.yandex.yandexlavka.exception.NotFoundException;
import ru.yandex.yandexlavka.exception.NotFoundResponse;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private final CustomExceptionLogger exceptionLogger;

    @Autowired
    public CustomExceptionHandler(CustomExceptionLogger exceptionLogger) {
        this.exceptionLogger = exceptionLogger;
    }

    @ExceptionHandler({
            BadRequestException.class,
            ConstraintViolationException.class
    })
    ResponseEntity<Object> handleBadRequest(@NonNull Exception ex) {
        exceptionLogger.info(ex);
        return ResponseEntity.badRequest().body(new BadRequestResponse());
    }

    @ExceptionHandler({
            NotFoundException.class
    })
    ResponseEntity<Object> handleNotFound(@NonNull Exception ex) {
        exceptionLogger.info(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex, Object body, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode, @NonNull WebRequest request
    ) {
        return handleBadRequest(ex);
    }
}
