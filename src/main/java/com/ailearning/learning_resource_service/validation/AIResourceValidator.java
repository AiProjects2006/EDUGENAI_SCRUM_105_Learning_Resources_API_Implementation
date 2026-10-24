package com.ailearning.learning_resource_service.validation;

import com.ailearning.learning_resource_service.dto.request.CreateAIResourceRequest;
import com.ailearning.learning_resource_service.dto.request.UpdateAIResourceRequest;
import com.ailearning.learning_resource_service.enums.AISourceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AIResourceValidator
        implements ConstraintValidator<ValidAIResource, Object> {

    @Override
    public boolean isValid(
            Object value,
            ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        Long moduleId = null;
        String title = null;
        AISourceType sourceType = null;
        String fileUrl = null;
        String externalFileId = null;

        if (value instanceof CreateAIResourceRequest request) {

            moduleId = request.getModuleId();
            title = request.getTitle();
            sourceType = request.getSourceType();
            fileUrl = request.getFileUrl();
            externalFileId = request.getExternalFileId();

        } else if (value instanceof UpdateAIResourceRequest request) {

            moduleId = request.getModuleId();
            title = request.getTitle();
            sourceType = request.getSourceType();
            fileUrl = request.getFileUrl();
            externalFileId = request.getExternalFileId();
        }

        /*
         * Let normal @NotNull / @NotBlank validation
         * handle these fields.
         */
        if (moduleId == null
                || title == null
                || title.isBlank()
                || sourceType == null) {
            return true;
        }

        boolean valid = true;

        context.disableDefaultConstraintViolation();

        switch (sourceType) {

            case PDF, DOCX -> {

                boolean hasFileUrl =
                        fileUrl != null && !fileUrl.isBlank();

                boolean hasExternalFileId =
                        externalFileId != null
                                && !externalFileId.isBlank();

                if (!hasFileUrl && !hasExternalFileId) {

                    addError(
                            context,
                            "fileUrl",
                            "Either fileUrl or externalFileId is required for "
                                    + sourceType + " resources"
                    );

                    valid = false;
                }
            }

            case WEB_PAGE -> {

                if (fileUrl == null || fileUrl.isBlank()) {

                    addError(
                            context,
                            "fileUrl",
                            "File URL is required for WEB_PAGE resources"
                    );

                    valid = false;
                }
            }

            case TEXT -> {
                /*
                 * TEXT resources do not require a file URL.
                 *
                 * The actual text content will be stored
                 * inside AIResourceChunk records.
                 */
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