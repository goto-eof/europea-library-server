package com.andreidodu.europealibrary.annotation.validation.impl;

import com.andreidodu.europealibrary.annotation.validation.ShouldBeAlwaysTrue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.BooleanUtils;

public class ShouldBeAlwaysTrueValidator implements ConstraintValidator<ShouldBeAlwaysTrue, Boolean> {
    public boolean isValid(Boolean value, ConstraintValidatorContext cxt) {
        return BooleanUtils.isTrue(value);
    }
}