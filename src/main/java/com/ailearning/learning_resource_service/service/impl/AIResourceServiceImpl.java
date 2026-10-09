package com.ailearning.learning_resource_service.service.impl;

import com.ailearning.learning_resource_service.dto.request.CreateAIResourceRequest;
import com.ailearning.learning_resource_service.dto.request.UpdateAIResourceRequest;
import com.ailearning.learning_resource_service.dto.response.AIContentChunkResponse;
import com.ailearning.learning_resource_service.dto.response.AIContentResourceResponse;
import com.ailearning.learning_resource_service.dto.response.AIContentResponse;
import com.ailearning.learning_resource_service.dto.response.AIResourceResponse;
import com.ailearning.learning_resource_service.entity.AIResource;
import com.ailearning.learning_resource_service.entity.AIResourceChunk;
import com.ailearning.learning_resource_service.enums.AIResourceStatus;
import com.ailearning.learning_resource_service.exception.ResourceNotFoundException;
import com.ailearning.learning_resource_service.repository.AIResourceChunkRepository;
import com.ailearning.learning_resource_service.repository.AIResourceRepository;
import com.ailearning.learning_resource_service.service.AIResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIResourceServiceImpl implements AIResourceService {

    private final AIResourceRepository aiResourceRepository;
    private final AIResourceChunkRepository chunkRepository;

    @Override
    public AIResourceResponse createAIResource(
            CreateAIResourceRequest request) {

        AIResource resource = AIResource.builder()
                .moduleId(request.getModuleId())
                .title(request.getTitle())
                .description(request.getDescription())
                .sourceType(request.getSourceType())
                .storageProvider(request.getStorageProvider())
                .externalFileId(request.getExternalFileId())
                .fileUrl(request.getFileUrl())
                .mimeType(request.getMimeType())
                .build();

        AIResource savedResource =
                aiResourceRepository.save(resource);

        return mapToResponse(savedResource);
    }

    @Override
    public AIResourceResponse getAIResource(
            Long aiResourceId) {

        AIResource resource = aiResourceRepository
                .findById(aiResourceId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "AI Resource not found: "
                                        + aiResourceId));

        return mapToResponse(resource);
    }

    @Override
    public List<AIResourceResponse> getAIResourcesByModule(
            Long moduleId) {

        return aiResourceRepository
                .findByModuleId(moduleId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AIResourceResponse mapToResponse(
            AIResource resource) {

        return AIResourceResponse.builder()
                .aiResourceId(resource.getAiResourceId())
                .moduleId(resource.getModuleId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .sourceType(resource.getSourceType())
                .storageProvider(resource.getStorageProvider())
                .externalFileId(resource.getExternalFileId())
                .fileUrl(resource.getFileUrl())
                .mimeType(resource.getMimeType())
                .status(resource.getStatus())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }

    @Override
    public AIContentResponse getAIContentByModule(Long moduleId) {

        List<AIResource> resources =
                aiResourceRepository
                        .findByModuleIdAndStatus(
                                moduleId,
                                AIResourceStatus.READY
                        );

        List<AIContentResourceResponse> resourceResponses =
                resources.stream()
                        .map(resource -> {

                            List<AIContentChunkResponse> chunks =
                                    chunkRepository
                                            .findByAiResourceAiResourceIdOrderByChunkIndex(
                                                    resource.getAiResourceId()
                                            )
                                            .stream()
                                            .map(this::mapChunkToResponse)
                                            .toList();

                            return AIContentResourceResponse.builder()
                                    .aiResourceId(
                                            resource.getAiResourceId())
                                    .title(resource.getTitle())
                                    .description(resource.getDescription())
                                    .sourceType(resource.getSourceType())
                                    .chunks(chunks)
                                    .build();
                        })
                        .toList();

        return AIContentResponse.builder()
                .moduleId(moduleId)
                .resources(resourceResponses)
                .build();
    }

    private AIContentChunkResponse mapChunkToResponse(
            AIResourceChunk chunk) {

        return AIContentChunkResponse.builder()
                .chunkIndex(chunk.getChunkIndex())
                .content(chunk.getContent())
                .pageStart(chunk.getPageStart())
                .pageEnd(chunk.getPageEnd())
                .sectionTitle(chunk.getSectionTitle())
                .build();
    }

    @Override
    public AIResourceResponse updateAIResource(
            Long aiResourceId,
            UpdateAIResourceRequest request) {

        AIResource resource = aiResourceRepository
                .findById(aiResourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "AI resource not found with id: " + aiResourceId
                        )
                );

        resource.setModuleId(request.getModuleId());
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setSourceType(request.getSourceType());
        resource.setStorageProvider(request.getStorageProvider());
        resource.setExternalFileId(request.getExternalFileId());
        resource.setFileUrl(request.getFileUrl());
        resource.setMimeType(request.getMimeType());

        AIResource updatedResource =
                aiResourceRepository.save(resource);

        return mapToResponse(updatedResource);
    }

    @Override
    @Transactional
    public void deleteAIResource(Long aiResourceId) {

        AIResource resource = aiResourceRepository
                .findById(aiResourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "AI resource not found with id: " + aiResourceId
                        )
                );

        chunkRepository.deleteByAiResourceAiResourceId(aiResourceId);

        aiResourceRepository.delete(resource);
    }

    @Override
    public void updateAIResourceStatus(
            Long aiResourceId,
            AIResourceStatus status) {

        AIResource resource = aiResourceRepository
                .findById(aiResourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "AI resource not found with id: "
                                        + aiResourceId
                        )
                );

        resource.setStatus(status);

        aiResourceRepository.save(resource);
    }
}