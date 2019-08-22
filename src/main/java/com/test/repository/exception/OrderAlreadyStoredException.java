package com.test.repository.exception;

public class OrderAlreadyStoredException extends IllegalArgumentException {
    public OrderAlreadyStoredException(final String message) {
        super(message);
    }
}
