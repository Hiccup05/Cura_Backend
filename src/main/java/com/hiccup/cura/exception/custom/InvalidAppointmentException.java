package com.hiccup.cura.exception.custom;

public class InvalidAppointmentException extends RuntimeException{
    public InvalidAppointmentException(String message){
        super(message);
    }
}
