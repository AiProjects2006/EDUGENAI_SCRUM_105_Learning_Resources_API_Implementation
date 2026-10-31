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
import com.ailearning.learning_resource_service.service.RedisCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AIResourceServiceImpl implements AIResourceService {

    private final AIResourceRepository aiResourceRepository;
    private final AIResourceChunkRepository chunkRepository;
    private final RedisCacheService redisCacheService;
    private final ObjectMapper objectMapper;

    @Value("${app.cache.resource.ttl-seconds:86400}")
    private long cacheTtlSeconds;

    public AIResourceServiceImpl(AIResourceRepository aiResourceRepository,
                                 AIResourceChunkRepository chunkRepository,
                                 RedisCacheService redisCacheService,
                                 ObjectMapper objectMapper) {
        this.aiResourceRepository = aiResourceRepository;
        this.chunkRepository = chunkRepository;
        this.redisCacheService = redisCacheService;
        this.objectMapper = objectMapper;
    }

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

        AIResourceResponse response = mapToResponse(savedResource);

        evictAICache(savedResource.getAiResourceId(), savedResource.getModuleId());

        return response;
    }

    @Override
    public AIResourceResponse getAIResource(
            Long aiResourceId) {

        String cacheKey = "ai_resource:id:" + aiResourceId;
        String cached = redisCacheService.get(cacheKey);
        if (cached != null && !cached.isBlank()) {
            try {
                log.info("Redis cache HIT for key [{}]", cacheKey);
                return objectMapper.readValue(cached, AIResourceResponse.class);
            } catch (Exception e) {
                log.warn("Failed to deserialize cached AI resource: {}", e.getMessage());
            }
        }

        log.info("Redis cache MISS for key [{}], reading from Resources DB", cacheKey);
        AIResource resource = aiResourceRepository
                .findById(aiResourceId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "AI Resource not found: "
                                        + aiResourceId));

        AIResourceResponse response = mapToResponse(resource);

        try {
            redisCacheService.set(cacheKey, objectMapper.writeValueAsString(response), cacheTtlSeconds);
        } catch (Exception e) {
            log.warn("Failed to cache AI resource in Redis: {}", e.getMessage());
        }

        return response;
    }

    @Override
    public List<AIResourceResponse> getAIResourcesByModule(
            Long moduleId) {

        String cacheKey = "ai_resource:module:" + moduleId;
        String cached = redisCacheService.get(cacheKey);
        if (cached != null && !cached.isBlank()) {
            try {
                log.info("Redis cache HIT for key [{}]", cacheKey);
                return objectMapper.readValue(cached, new TypeReference<List<AIResourceResponse>>() {});
            } catch (Exception e) {
                log.warn("Failed to deserialize cached AI resources: {}", e.getMessage());
            }
        }

        log.info("Redis cache MISS for key [{}], reading from Resources DB", cacheKey);
        List<AIResourceResponse> list = aiResourceRepository
                .findByModuleId(moduleId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        try {
            redisCacheService.set(cacheKey, objectMapper.writeValueAsString(list), cacheTtlSeconds);
        } catch (Exception e) {
            log.warn("Failed to cache AI resources in Redis: {}", e.getMessage());
        }

        return list;
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

        String cacheKey = "ai_content:module:" + moduleId;
        String cached = redisCacheService.get(cacheKey);
        if (cached != null && !cached.isBlank()) {
            try {
                log.info("Redis cache HIT for key [{}]", cacheKey);
                return objectMapper.readValue(cached, AIContentResponse.class);
            } catch (Exception e) {
                log.warn("Failed to deserialize cached AI content: {}", e.getMessage());
            }
        }

        log.info("Redis cache MISS for key [{}], reading from Resources DB", cacheKey);
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

        AIContentResponse response = AIContentResponse.builder()
                .moduleId(moduleId)
                .resources(resourceResponses)
                .build();

        try {
            redisCacheService.set(cacheKey, objectMapper.writeValueAsString(response), cacheTtlSeconds);
        } catch (Exception e) {
            log.warn("Failed to cache AI content in Redis: {}", e.getMessage());
        }

        return response;
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

        AIResourceResponse response = mapToResponse(updatedResource);

        evictAICache(aiResourceId, updatedResource.getModuleId());

        return response;
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

        Long moduleId = resource.getModuleId();
        chunkRepository.deleteByAiResourceAiResourceId(aiResourceId);
        aiResourceRepository.delete(resource);

        evictAICache(aiResourceId, moduleId);
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

        evictAICache(aiResourceId, resource.getModuleId());
    }

    private void evictAICache(Long aiResourceId, Long moduleId) {
        if (aiResourceId != null) {
            redisCacheService.delete("ai_resource:id:" + aiResourceId);
        }
        if (moduleId != null) {
            redisCacheService.delete("ai_resource:module:" + moduleId);
            redisCacheService.delete("ai_content:module:" + moduleId);
        }
    }
}