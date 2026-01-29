package com.eduribeiro8.LilMarket.rest.exception;

public class DuplicateBarcodeException extends RuntimeException {
    public DuplicateBarcodeException(String message) {
        super(message);
    }
}
