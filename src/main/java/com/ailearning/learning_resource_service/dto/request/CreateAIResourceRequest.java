package com.ailearning.learning_resource_service.dto.request;

import com.ailearning.learning_resource_service.enums.AISourceType;
import com.ailearning.learning_resource_service.enums.StorageProvider;
import com.ailearning.learning_resource_service.validation.ValidAIResource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidAIResource
public class CreateAIResourceRequest {

    @NotNull
    private Long moduleId;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private AISourceType sourceType;

    private StorageProvider storageProvider;

    private String externalFileId;

    private String fileUrl;

    private String mimeType;
}