package com.hiccup.cura.exception.custom;

public class DuplicateEntryException extends RuntimeException{
    public DuplicateEntryException(String msg){
        super(msg);
    }
}
