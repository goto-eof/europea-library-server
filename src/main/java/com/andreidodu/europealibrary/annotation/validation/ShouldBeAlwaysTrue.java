package com.andreidodu.europealibrary.annotation.validation;

import com.andreidodu.europealibrary.annotation.validation.impl.ShouldBeAlwaysTrueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = ShouldBeAlwaysTrueValidator.class)
public @interface ShouldBeAlwaysTrue {
    String message() default "Invalid value: this field should be always true";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}