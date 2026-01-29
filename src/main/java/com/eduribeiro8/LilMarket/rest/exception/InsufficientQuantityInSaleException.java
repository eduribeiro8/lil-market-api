package com.eduribeiro8.LilMarket.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InsufficientQuantityInSaleException extends RuntimeException{
    public InsufficientQuantityInSaleException(){
        super("Insufficient Quantity in Sale");
    }

    public InsufficientQuantityInSaleException(String message) {
        super(message);
    }
}
