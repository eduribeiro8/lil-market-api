package com.eduribeiro8.LilMarket.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(){
        super("Product not found");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
