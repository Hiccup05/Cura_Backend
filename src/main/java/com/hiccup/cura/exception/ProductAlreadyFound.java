package com.hiccup.cura.exception;

public class ProductAlreadyFound extends RuntimeException{
    public ProductAlreadyFound(String message){
        super(message);
    }
}
