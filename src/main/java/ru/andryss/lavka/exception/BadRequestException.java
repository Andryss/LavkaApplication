package ru.andryss.lavka.exception;

public class BadRequestException extends RuntimeException {

    public static final BadRequestException EMPTY = new BadRequestException();

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
