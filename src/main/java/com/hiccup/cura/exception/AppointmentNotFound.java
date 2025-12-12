package com.hiccup.cura.exception;

public class AppointmentNotFound extends RuntimeException{
    public AppointmentNotFound(String message){
        super(message);
    }
}
