package com.andreidodu.europealibrary.exception;

public class FurtherStepNeededException extends RuntimeException {
    public FurtherStepNeededException(String message) {
        super(message);
    }

    public FurtherStepNeededException(String message, Throwable e) {
        super(message, e);
    }

    public FurtherStepNeededException(Throwable e) {
        super(e);
    }
}
