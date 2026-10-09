package com.ailearning.learning_resource_service.dto.request;

import com.ailearning.learning_resource_service.enums.ResourceType;
import com.ailearning.learning_resource_service.enums.StorageProvider;
import com.ailearning.learning_resource_service.validation.ValidStudentResource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@ValidStudentResource
@Getter
@Setter
public class CreateStudentResourceRequest {

    @NotNull
    private Long moduleId;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private ResourceType resourceType;

    private String content;

    private String fileUrl;

    private String externalFileId;

    private StorageProvider storageProvider;

    private String mimeType;

    private Integer sortOrder;
}