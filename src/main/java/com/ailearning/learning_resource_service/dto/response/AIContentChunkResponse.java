package com.ailearning.learning_resource_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIContentChunkResponse {

    private Integer chunkIndex;
    private String content;
    private Integer pageStart;
    private Integer pageEnd;
    private String sectionTitle;
}