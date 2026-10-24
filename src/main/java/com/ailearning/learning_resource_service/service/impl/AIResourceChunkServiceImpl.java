package com.ailearning.learning_resource_service.service.impl;

import com.ailearning.learning_resource_service.dto.request.CreateAIResourceChunkRequest;
import com.ailearning.learning_resource_service.dto.response.AIResourceChunkResponse;
import com.ailearning.learning_resource_service.entity.AIResource;
import com.ailearning.learning_resource_service.entity.AIResourceChunk;
import com.ailearning.learning_resource_service.exception.ResourceNotFoundException;
import com.ailearning.learning_resource_service.repository.AIResourceChunkRepository;
import com.ailearning.learning_resource_service.repository.AIResourceRepository;
import com.ailearning.learning_resource_service.service.AIResourceChunkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIResourceChunkServiceImpl
        implements AIResourceChunkService {

    private final AIResourceChunkRepository chunkRepository;
    private final AIResourceRepository aiResourceRepository;

    @Override
    public AIResourceChunkResponse createChunk(
            Long aiResourceId,
            CreateAIResourceChunkRequest request) {

        AIResource aiResource = aiResourceRepository
                .findById(aiResourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "AI resource not found with id: " + aiResourceId
                        )
                );

        AIResourceChunk chunk = AIResourceChunk.builder()
                .aiResource(aiResource)
                .chunkIndex(request.getChunkIndex())
                .content(request.getContent())
                .pageStart(request.getPageStart())
                .pageEnd(request.getPageEnd())
                .sectionTitle(request.getSectionTitle())
                .build();

        AIResourceChunk savedChunk =
                chunkRepository.save(chunk);

        return mapToResponse(savedChunk);
    }

    @Override
    public List<AIResourceChunkResponse> getChunks(
            Long aiResourceId) {

        if (!aiResourceRepository.existsById(aiResourceId)) {
            throw new ResourceNotFoundException(
                    "AI resource not found with id: " + aiResourceId
            );
        }

        return chunkRepository
                .findByAiResourceAiResourceIdOrderByChunkIndex(
                        aiResourceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AIResourceChunkResponse mapToResponse(
            AIResourceChunk chunk) {

        return AIResourceChunkResponse.builder()
                .id(chunk.getId())
                .aiResourceId(
                        chunk.getAiResource()
                                .getAiResourceId())
                .chunkIndex(chunk.getChunkIndex())
                .content(chunk.getContent())
                .pageStart(chunk.getPageStart())
                .pageEnd(chunk.getPageEnd())
                .sectionTitle(chunk.getSectionTitle())
                .build();
    }
}