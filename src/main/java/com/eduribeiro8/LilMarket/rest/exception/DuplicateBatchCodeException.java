package com.eduribeiro8.LilMarket.rest.exception;

public class DuplicateBatchCodeException extends RuntimeException {
    public DuplicateBatchCodeException(String message) {
        super(message);
    }
}
