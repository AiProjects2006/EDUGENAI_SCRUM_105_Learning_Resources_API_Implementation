package com.ailearning.learning_resource_service.dto.request;

import com.ailearning.learning_resource_service.enums.AIResourceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAIResourceStatusRequest {

    @NotNull
    private AIResourceStatus status;
}