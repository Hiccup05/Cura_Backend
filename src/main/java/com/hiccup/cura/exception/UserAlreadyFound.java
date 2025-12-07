package com.hiccup.cura.exception;

public class UserAlreadyFound  extends RuntimeException{
    public UserAlreadyFound(String message){
        super(message);
    }
}
