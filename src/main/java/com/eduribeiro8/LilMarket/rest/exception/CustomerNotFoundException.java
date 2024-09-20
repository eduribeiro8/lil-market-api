package com.eduribeiro8.LilMarket.rest.exception;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException(){
        super("Customer not found");
    }
}
