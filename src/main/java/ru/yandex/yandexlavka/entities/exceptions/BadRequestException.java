package ru.yandex.yandexlavka.entities.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        this((String) null);
    }

    public BadRequestException(String message) {
        this(message, null);
    }

    public BadRequestException(Throwable cause) {
        this(null, cause);
    }

    public BadRequestException(String message, Throwable cause) {
        this(message, cause, true, false);
    }

    public BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
