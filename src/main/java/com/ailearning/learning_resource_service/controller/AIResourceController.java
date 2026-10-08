package com.ailearning.learning_resource_service.controller;

import com.ailearning.learning_resource_service.dto.request.CreateAIResourceRequest;
import com.ailearning.learning_resource_service.dto.request.UpdateAIResourceRequest;
import com.ailearning.learning_resource_service.dto.request.UpdateAIResourceStatusRequest;
import com.ailearning.learning_resource_service.dto.response.AIContentResponse;
import com.ailearning.learning_resource_service.dto.response.AIResourceResponse;
import com.ailearning.learning_resource_service.service.AIResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-resources")
@RequiredArgsConstructor
@Tag(name = "AI Resources")
public class AIResourceController {

    private final AIResourceService aiResourceService;

    @PostMapping
    @Operation(summary = "Create an AI resource")
    public ResponseEntity<AIResourceResponse> createAIResource(
            @Valid @RequestBody CreateAIResourceRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aiResourceService.createAIResource(request));
    }

    @GetMapping("/{aiResourceId}")
    @Operation(summary = "Get an AI resource by ID")
    public ResponseEntity<AIResourceResponse> getAIResource(
            @PathVariable Long aiResourceId) {

        return ResponseEntity.ok(
                aiResourceService.getAIResource(aiResourceId));
    }

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Get AI resources for a module")
    public ResponseEntity<List<AIResourceResponse>> getAIResourcesByModule(
            @PathVariable Long moduleId) {

        return ResponseEntity.ok(
                aiResourceService
                        .getAIResourcesByModule(moduleId));
    }

    @GetMapping("/module/{moduleId}/content")
    @Operation(
            summary = "Get AI-ready content for a module",
            description = "Returns READY AI resources and their content chunks"
    )
    public ResponseEntity<AIContentResponse> getAIContentByModule(
            @PathVariable Long moduleId) {

        return ResponseEntity.ok(
                aiResourceService.getAIContentByModule(moduleId)
        );
    }

    @PutMapping("/{aiResourceId}")
    @Operation(
            summary = "Update an AI resource",
            description = "Updates AI resource metadata without changing its processing status"
    )
    public ResponseEntity<AIResourceResponse> updateAIResource(
            @PathVariable Long aiResourceId,
            @Valid @RequestBody UpdateAIResourceRequest request) {

        return ResponseEntity.ok(
                aiResourceService.updateAIResource(
                        aiResourceId,
                        request
                )
        );
    }

    @DeleteMapping("/{aiResourceId}")
    @Operation(
            summary = "Delete an AI resource",
            description = "Deletes an AI resource and its associated chunks"
    )
    public ResponseEntity<Void> deleteAIResource(
            @PathVariable Long aiResourceId) {

        aiResourceService.deleteAIResource(aiResourceId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{aiResourceId}/status")
    @Operation(
            summary = "Update AI resource processing status",
            description = "Updates the processing status of an AI resource"
    )
    public ResponseEntity<Void> updateAIResourceStatus(
            @PathVariable Long aiResourceId,
            @Valid @RequestBody UpdateAIResourceStatusRequest request) {

        aiResourceService.updateAIResourceStatus(
                aiResourceId,
                request.getStatus()
        );

        return ResponseEntity.noContent().build();
    }
}