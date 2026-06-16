package com.hiccup.cura.exception.custom;

public class InvalidReactivationTokenException extends RuntimeException {
    public InvalidReactivationTokenException(String message) {
        super(message);
    }
}
