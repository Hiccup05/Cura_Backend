package com.hiccup.cura.exception;

public class AppointmentAlreadyExists extends RuntimeException{
    public AppointmentAlreadyExists(String message){
        super(message);
    }

}
