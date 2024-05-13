package com.andreidodu.europealibrary.util;

import com.andreidodu.europealibrary.exception.ValidationException;

public class ValidationUtil {
    public static <T> void assertNotNull(T value, String message) {
        if (value == null) {
            throw new ValidationException(message);
        }
    }

    public static <T extends Boolean> void assertNotNullAndTrue(T value, String message) {
        if (value == null || Boolean.FALSE.equals(value)) {
            throw new ValidationException(message);
        }
    }

}
