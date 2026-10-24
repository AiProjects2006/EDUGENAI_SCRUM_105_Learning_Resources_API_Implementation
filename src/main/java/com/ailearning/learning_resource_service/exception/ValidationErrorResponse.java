package com.ailearning.learning_resource_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ValidationErrorResponse {

    private int status;
    private String message;
    private Map<String, String> errors;
}