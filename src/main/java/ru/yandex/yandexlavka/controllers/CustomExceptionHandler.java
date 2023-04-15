package ru.yandex.yandexlavka.controllers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.yandexlavka.entities.exceptions.BadRequestException;
import ru.yandex.yandexlavka.entities.exceptions.BadRequestResponse;
import ru.yandex.yandexlavka.entities.exceptions.NotFoundException;
import ru.yandex.yandexlavka.entities.exceptions.NotFoundResponse;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ BadRequestException.class, ConstraintViolationException.class })
    ResponseEntity<BadRequestResponse> handleBadRequest() {
        return ResponseEntity.badRequest().body(new BadRequestResponse());
    }

    @ExceptionHandler({ NotFoundException.class })
    ResponseEntity<NotFoundResponse> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse());
    }

}
