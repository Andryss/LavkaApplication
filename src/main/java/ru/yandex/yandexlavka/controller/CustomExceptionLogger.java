package ru.yandex.yandexlavka.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomExceptionLogger {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void info(Exception exception) {
        String exceptionClassName = exception.getClass().getSimpleName();
        String exceptionMessage = exception.getMessage();
        logger.info(String.format("%s: %s", exceptionClassName, exceptionMessage));
    }

}
