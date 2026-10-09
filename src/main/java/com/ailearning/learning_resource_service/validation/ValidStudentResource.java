package com.ailearning.learning_resource_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StudentResourceValidator.class)
@Documented
public @interface ValidStudentResource {

    String message() default "Invalid student resource";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}