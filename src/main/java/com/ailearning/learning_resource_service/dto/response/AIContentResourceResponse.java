package com.ailearning.learning_resource_service.dto.response;

import com.ailearning.learning_resource_service.enums.AISourceType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AIContentResourceResponse {

    private Long aiResourceId;
    private String title;
    private String description;
    private AISourceType sourceType;

    private List<AIContentChunkResponse> chunks;
}