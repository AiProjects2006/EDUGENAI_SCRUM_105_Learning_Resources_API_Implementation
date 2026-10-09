package com.ailearning.learning_resource_service.dto.response;

import com.ailearning.learning_resource_service.enums.ResourceStatus;
import com.ailearning.learning_resource_service.enums.ResourceType;
import com.ailearning.learning_resource_service.enums.StorageProvider;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudentResourceResponse {

    private Long resourceId;
    private Long moduleId;
    private String title;
    private String description;
    private ResourceType resourceType;
    private String content;
    private String fileUrl;
    private String externalFileId;
    private StorageProvider storageProvider;
    private String mimeType;
    private Integer sortOrder;
    private ResourceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}