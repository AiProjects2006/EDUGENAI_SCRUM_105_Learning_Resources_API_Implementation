package com.ailearning.learning_resource_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AIContentResponse {

    private Long moduleId;

    private List<AIContentResourceResponse> resources;
}