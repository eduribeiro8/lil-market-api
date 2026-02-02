package com.eduribeiro8.LilMarket.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDateIntervalException extends RuntimeException {
    public InvalidDateIntervalException(String message) {
        super(message);
    }
}
