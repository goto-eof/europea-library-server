package com.andreidodu.europealibrary.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable e) {
        super(message, e);
    }

    public ValidationException(Throwable e) {
        super(e);
    }
}
