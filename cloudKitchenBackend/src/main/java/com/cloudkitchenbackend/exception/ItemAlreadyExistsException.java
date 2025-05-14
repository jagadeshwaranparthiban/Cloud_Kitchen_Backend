package com.cloudkitchenbackend.exception;


public class ItemAlreadyExistsException extends RuntimeException {
    public ItemAlreadyExistsException(String msg){
        super(msg);
    }
}
