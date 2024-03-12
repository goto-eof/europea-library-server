package com.andreidodu.europalibrary.exception;

import java.io.IOException;

public class ApplicationException extends RuntimeException {
    public ApplicationException(Throwable e) {
        super(e);
    }
}
