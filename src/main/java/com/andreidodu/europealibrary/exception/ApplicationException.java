package com.andreidodu.europealibrary.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Throwable e) {
        super(e);
    }
}
