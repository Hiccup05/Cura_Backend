package com.hiccup.cura.exception.custom;

public class StaffAccountDeactivatedException extends  RuntimeException{
    public StaffAccountDeactivatedException(String message){
        super(message);
    }
}
