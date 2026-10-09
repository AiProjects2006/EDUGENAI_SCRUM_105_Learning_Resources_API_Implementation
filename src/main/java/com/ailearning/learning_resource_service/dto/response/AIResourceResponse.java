package com.ailearning.learning_resource_service.dto.response;

import com.ailearning.learning_resource_service.enums.AIResourceStatus;
import com.ailearning.learning_resource_service.enums.AISourceType;
import com.ailearning.learning_resource_service.enums.StorageProvider;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AIResourceResponse {

    private Long aiResourceId;
    private Long moduleId;
    private String title;
    private String description;
    private AISourceType sourceType;
    private StorageProvider storageProvider;
    private String externalFileId;
    private String fileUrl;
    private String mimeType;
    private AIResourceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}