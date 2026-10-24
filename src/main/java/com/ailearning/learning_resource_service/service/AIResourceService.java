package com.ailearning.learning_resource_service.service;

import com.ailearning.learning_resource_service.dto.request.CreateAIResourceRequest;
import com.ailearning.learning_resource_service.dto.request.UpdateAIResourceRequest;
import com.ailearning.learning_resource_service.dto.response.AIContentResponse;
import com.ailearning.learning_resource_service.dto.response.AIResourceResponse;
import com.ailearning.learning_resource_service.enums.AIResourceStatus;

import java.util.List;

public interface AIResourceService {

    AIResourceResponse createAIResource(
            CreateAIResourceRequest request
    );

    AIResourceResponse getAIResource(Long aiResourceId);

    List<AIResourceResponse> getAIResourcesByModule(Long moduleId);

    AIContentResponse getAIContentByModule(Long moduleId);

    AIResourceResponse updateAIResource(
            Long aiResourceId,
            UpdateAIResourceRequest request
    );

    void deleteAIResource(Long aiResourceId);

    void updateAIResourceStatus(
            Long aiResourceId,
            AIResourceStatus status
    );
}