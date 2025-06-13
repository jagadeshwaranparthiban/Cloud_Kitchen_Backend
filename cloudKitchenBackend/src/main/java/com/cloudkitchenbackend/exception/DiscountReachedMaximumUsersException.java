package com.cloudkitchenbackend.exception;

public class DiscountReachedMaximumUsersException extends RuntimeException {
    public DiscountReachedMaximumUsersException(String message) {
        super(message);
    }
}
