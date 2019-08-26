package com.test.repository.exception;

public class OrderAlreadyStoredException extends IllegalStateException {
    public OrderAlreadyStoredException(final String message) {
        super(message);
    }
}
