package com.hiccup.cura.exception.custom;

public class DuplicatePaymentException extends RuntimeException{
    public DuplicatePaymentException(String message){
        super(message);
    }
}
