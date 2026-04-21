package com.hiccup.cura.exception.custom;

public class InvalidBookingTimeException extends RuntimeException{
    public InvalidBookingTimeException(String message) {
        super(message);
    }
}
