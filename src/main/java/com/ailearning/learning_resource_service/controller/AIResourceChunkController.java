package com.ailearning.learning_resource_service.controller;

import com.ailearning.learning_resource_service.dto.request.CreateAIResourceChunkRequest;
import com.ailearning.learning_resource_service.dto.response.AIResourceChunkResponse;
import com.ailearning.learning_resource_service.service.AIResourceChunkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-resources/{aiResourceId}/chunks")
@RequiredArgsConstructor
@Tag(name = "AI Resource Chunks")
public class AIResourceChunkController {

    private final AIResourceChunkService chunkService;

    @PostMapping
    @Operation(summary = "Create an AI resource chunk")
    public ResponseEntity<AIResourceChunkResponse> createChunk(
            @PathVariable Long aiResourceId,
            @Valid @RequestBody CreateAIResourceChunkRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(chunkService.createChunk(
                        aiResourceId,
                        request));
    }

    @GetMapping
    @Operation(summary = "Get chunks for an AI resource")
    public ResponseEntity<List<AIResourceChunkResponse>> getChunks(
            @PathVariable Long aiResourceId) {

        return ResponseEntity.ok(
                chunkService.getChunks(aiResourceId));
    }
}