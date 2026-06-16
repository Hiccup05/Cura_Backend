package com.hiccup.cura.exception.custom;

public class ReactivationTokenExpiredException extends RuntimeException {
    public ReactivationTokenExpiredException(String message) {
        super(message);
    }
}
