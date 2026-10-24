package com.ailearning.learning_resource_service.dto.response;

import com.ailearning.learning_resource_service.enums.AISourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIContentResourceResponse {

    private Long aiResourceId;
    private String title;
    private String description;
    private AISourceType sourceType;

    private List<AIContentChunkResponse> chunks;
}