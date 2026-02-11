package com.eduribeiro8.LilMarket.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RestockNotFoundException extends RuntimeException {
    public RestockNotFoundException(String message) {
        super(message);
    }
}
