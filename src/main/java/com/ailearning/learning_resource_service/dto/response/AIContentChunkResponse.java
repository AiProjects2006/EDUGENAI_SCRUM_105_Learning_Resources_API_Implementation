package com.ailearning.learning_resource_service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AIContentChunkResponse {

    private Integer chunkIndex;
    private String content;
    private Integer pageStart;
    private Integer pageEnd;
    private String sectionTitle;
}