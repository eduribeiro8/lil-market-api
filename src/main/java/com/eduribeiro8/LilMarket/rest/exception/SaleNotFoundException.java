package com.eduribeiro8.LilMarket.rest.exception;

public class SaleNotFoundException extends RuntimeException{
    public SaleNotFoundException(){
        super("Sale not found");
    }

    public SaleNotFoundException(String message){
        super(message);
    }
}
