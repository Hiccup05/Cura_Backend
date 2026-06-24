package com.hiccup.cura.exception.custom;

public class PaymentProviderNotSupported extends RuntimeException{
    public PaymentProviderNotSupported(String message){
        super(message);
    }
}
