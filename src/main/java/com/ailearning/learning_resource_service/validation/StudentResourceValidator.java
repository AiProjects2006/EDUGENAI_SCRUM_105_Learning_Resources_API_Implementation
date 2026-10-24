package com.ailearning.learning_resource_service.validation;

import com.ailearning.learning_resource_service.dto.request.CreateStudentResourceRequest;
import com.ailearning.learning_resource_service.dto.request.UpdateStudentResourceRequest;
import com.ailearning.learning_resource_service.enums.ResourceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StudentResourceValidator
        implements ConstraintValidator<ValidStudentResource, Object> {

    @Override
    public boolean isValid(
            Object value,
            ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        Long moduleId = null;
        String title = null;
        ResourceType resourceType = null;
        String content = null;
        String fileUrl = null;
        String externalFileId = null;

        if (value instanceof CreateStudentResourceRequest request) {
            moduleId = request.getModuleId();
            title = request.getTitle();
            resourceType = request.getResourceType();
            content = request.getContent();
            fileUrl = request.getFileUrl();
            externalFileId = request.getExternalFileId();

        } else if (value instanceof UpdateStudentResourceRequest request) {
            moduleId = request.getModuleId();
            title = request.getTitle();
            resourceType = request.getResourceType();
            content = request.getContent();
            fileUrl = request.getFileUrl();
            externalFileId = request.getExternalFileId();
        }

        /*
         * Basic @NotNull / @NotBlank validation handles these.
         * We don't duplicate those errors here.
         */
        if (moduleId == null || title == null || title.isBlank()
                || resourceType == null) {
            return true;
        }

        boolean valid = true;

        context.disableDefaultConstraintViolation();

        switch (resourceType) {

            case NOTE -> {
                if (content == null || content.isBlank()) {
                    addError(
                            context,
                            "content",
                            "Content is required for NOTE resources"
                    );
                    valid = false;
                }
            }

            case LINK -> {
                if (fileUrl == null || fileUrl.isBlank()) {
                    addError(
                            context,
                            "fileUrl",
                            "File URL is required for LINK resources"
                    );
                    valid = false;
                }
            }

            case PDF, IMAGE, VIDEO -> {
                boolean hasFile =
                        fileUrl != null && !fileUrl.isBlank();

                boolean hasExternalFile =
                        externalFileId != null
                                && !externalFileId.isBlank();

                if (!hasFile && !hasExternalFile) {
                    addError(
                            context,
                            "fileUrl",
                            "Either fileUrl or externalFileId is required for "
                                    + resourceType + " resources"
                    );
                    valid = false;
                }
            }
        }

        return valid;
    }

    private void addError(
            ConstraintValidatorContext context,
            String field,
            String message) {

        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}