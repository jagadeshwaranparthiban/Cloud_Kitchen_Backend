package com.cloudkitchenbackend.exception;

public class DiscountCodeNotFoundException extends RuntimeException {
    public DiscountCodeNotFoundException(String message) {
        super(message);
    }
}
