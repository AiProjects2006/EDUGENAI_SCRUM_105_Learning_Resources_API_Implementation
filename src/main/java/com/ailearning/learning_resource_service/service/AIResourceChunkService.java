package com.ailearning.learning_resource_service.service;

import com.ailearning.learning_resource_service.dto.request.CreateAIResourceChunkRequest;
import com.ailearning.learning_resource_service.dto.response.AIResourceChunkResponse;

import java.util.List;

public interface AIResourceChunkService {

    AIResourceChunkResponse createChunk(
            Long aiResourceId,
            CreateAIResourceChunkRequest request
    );

    List<AIResourceChunkResponse> getChunks(
            Long aiResourceId
    );
}