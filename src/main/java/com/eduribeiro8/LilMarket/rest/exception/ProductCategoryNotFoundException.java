package com.eduribeiro8.LilMarket.rest.exception;

public class ProductCategoryNotFoundException extends RuntimeException {
    public ProductCategoryNotFoundException() {
        super("Product category not found");
    }

    public ProductCategoryNotFoundException(String message) {
        super(message);
    }
}
