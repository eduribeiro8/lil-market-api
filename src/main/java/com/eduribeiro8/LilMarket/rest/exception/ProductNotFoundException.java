package com.eduribeiro8.LilMarket.rest.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(){
        super("Product not found");
    }
}
