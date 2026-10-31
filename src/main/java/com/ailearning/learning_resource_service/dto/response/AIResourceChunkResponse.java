package com.ailearning.learning_resource_service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AIResourceChunkResponse {

    private Long id;
    private Long aiResourceId;
    private Integer chunkIndex;
    private String content;
    private Integer pageStart;
    private Integer pageEnd;
    private String sectionTitle;
}