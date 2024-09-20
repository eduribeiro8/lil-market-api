package com.eduribeiro8.LilMarket.rest.exception;

public class InsufficientQuantityInSaleException extends RuntimeException{
    public InsufficientQuantityInSaleException(){
        super("Insufficient Quantity in Sale");
    }
}
