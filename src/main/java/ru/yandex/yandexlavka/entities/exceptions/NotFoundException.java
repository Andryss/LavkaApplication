package ru.yandex.yandexlavka.entities.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        this((String) null);
    }

    public NotFoundException(String message) {
        this(message, null);
    }

    public NotFoundException(Throwable cause) {
        this(null, cause);
    }

    public NotFoundException(String message, Throwable cause) {
        this(message, cause, true, false);
    }

    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}