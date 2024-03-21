package com.andreidodu.europealibrary.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable e) {
        super(message, e);
    }

    public ApplicationException(Throwable e) {
        super(e);
    }
}
