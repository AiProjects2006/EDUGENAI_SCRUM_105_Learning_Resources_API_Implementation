package com.ailearning.learning_resource_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AIResourceValidator.class)
@Documented
public @interface ValidAIResource {

    String message() default "Invalid AI resource";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}