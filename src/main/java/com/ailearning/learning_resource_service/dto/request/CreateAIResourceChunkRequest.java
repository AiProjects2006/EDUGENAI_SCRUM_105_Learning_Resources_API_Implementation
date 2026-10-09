package com.ailearning.learning_resource_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAIResourceChunkRequest {

    @NotNull
    @Min(0)
    private Integer chunkIndex;

    @NotBlank
    private String content;

    private Integer pageStart;

    private Integer pageEnd;

    private String sectionTitle;
}