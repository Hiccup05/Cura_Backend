package com.hiccup.cura.exception.custom;

public class PatientAccountDeactivatedException extends RuntimeException{
    public PatientAccountDeactivatedException(String message){
        super(message);
    }
}
