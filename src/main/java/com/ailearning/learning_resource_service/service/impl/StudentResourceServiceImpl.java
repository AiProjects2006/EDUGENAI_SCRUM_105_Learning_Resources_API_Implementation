package com.ailearning.learning_resource_service.service.impl;

import com.ailearning.learning_resource_service.dto.request.CreateStudentResourceRequest;
import com.ailearning.learning_resource_service.dto.request.UpdateStudentResourceRequest;
import com.ailearning.learning_resource_service.dto.response.StudentResourceResponse;
import com.ailearning.learning_resource_service.entity.StudentResource;
import com.ailearning.learning_resource_service.exception.ResourceNotFoundException;
import com.ailearning.learning_resource_service.repository.StudentResourceRepository;
import com.ailearning.learning_resource_service.service.RedisCacheService;
import com.ailearning.learning_resource_service.service.StudentResourceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StudentResourceServiceImpl implements StudentResourceService {

    private final StudentResourceRepository studentResourceRepository;
    private final RedisCacheService redisCacheService;
    private final ObjectMapper objectMapper;

    @Value("${app.cache.resource.ttl-seconds:86400}")
    private long cacheTtlSeconds;

    public StudentResourceServiceImpl(StudentResourceRepository studentResourceRepository,
                                      RedisCacheService redisCacheService,
                                      ObjectMapper objectMapper) {
        this.studentResourceRepository = studentResourceRepository;
        this.redisCacheService = redisCacheService;
        this.objectMapper = objectMapper;
    }

    @Override
    public StudentResourceResponse createResource(
            CreateStudentResourceRequest request) {

        StudentResource resource = StudentResource.builder()
                .moduleId(request.getModuleId())
                .title(request.getTitle())
                .description(request.getDescription())
                .resourceType(request.getResourceType())
                .content(request.getContent())
                .fileUrl(request.getFileUrl())
                .externalFileId(request.getExternalFileId())
                .storageProvider(request.getStorageProvider())
                .mimeType(request.getMimeType())
                .sortOrder(request.getSortOrder())
                .build();

        StudentResource savedResource = studentResourceRepository.save(resource);
        StudentResourceResponse response = mapToResponse(savedResource);

        evictResourceCache(savedResource.getResourceId(), savedResource.getModuleId());

        return response;
    }

    @Override
    public List<StudentResourceResponse> getResourcesByModule(
            Long moduleId) {

        String cacheKey = "resource:module:" + moduleId;
        String cached = redisCacheService.get(cacheKey);
        if (cached != null && !cached.isBlank()) {
            try {
                log.info("Redis cache HIT for key [{}]", cacheKey);
                return objectMapper.readValue(cached, new TypeReference<List<StudentResourceResponse>>() {});
            } catch (Exception e) {
                log.warn("Failed to deserialize cached module resources: {}", e.getMessage());
            }
        }

        log.info("Redis cache MISS for key [{}], reading from Resources DB", cacheKey);
        List<StudentResourceResponse> resources = studentResourceRepository
                .findByModuleId(moduleId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        try {
            redisCacheService.set(cacheKey, objectMapper.writeValueAsString(resources), cacheTtlSeconds);
        } catch (Exception e) {
            log.warn("Failed to cache module resources in Redis: {}", e.getMessage());
        }

        return resources;
    }

    @Override
    public StudentResourceResponse getResource(Long resourceId) {

        String cacheKey = "resource:id:" + resourceId;
        String cached = redisCacheService.get(cacheKey);
        if (cached != null && !cached.isBlank()) {
            try {
                log.info("Redis cache HIT for key [{}]", cacheKey);
                return objectMapper.readValue(cached, StudentResourceResponse.class);
            } catch (Exception e) {
                log.warn("Failed to deserialize cached resource: {}", e.getMessage());
            }
        }

        log.info("Redis cache MISS for key [{}], reading from Resources DB", cacheKey);
        StudentResource resource = studentResourceRepository
                .findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student resource not found with id: " + resourceId));

        StudentResourceResponse response = mapToResponse(resource);

        try {
            redisCacheService.set(cacheKey, objectMapper.writeValueAsString(response), cacheTtlSeconds);
        } catch (Exception e) {
            log.warn("Failed to cache resource in Redis: {}", e.getMessage());
        }

        return response;
    }

    private StudentResourceResponse mapToResponse(
            StudentResource resource) {

        return StudentResourceResponse.builder()
                .resourceId(resource.getResourceId())
                .moduleId(resource.getModuleId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .resourceType(resource.getResourceType())
                .content(resource.getContent())
                .fileUrl(resource.getFileUrl())
                .externalFileId(resource.getExternalFileId())
                .storageProvider(resource.getStorageProvider())
                .mimeType(resource.getMimeType())
                .sortOrder(resource.getSortOrder())
                .status(resource.getStatus())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }

    @Override
    public StudentResourceResponse updateResource(
            Long resourceId,
            UpdateStudentResourceRequest request) {

        StudentResource resource = studentResourceRepository
                .findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student resource not found with id: " + resourceId));

        resource.setModuleId(request.getModuleId());
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setResourceType(request.getResourceType());
        resource.setContent(request.getContent());
        resource.setFileUrl(request.getFileUrl());
        resource.setExternalFileId(request.getExternalFileId());
        resource.setStorageProvider(request.getStorageProvider());
        resource.setMimeType(request.getMimeType());
        resource.setSortOrder(request.getSortOrder());

        StudentResource updatedResource = studentResourceRepository.save(resource);
        StudentResourceResponse response = mapToResponse(updatedResource);

        evictResourceCache(resourceId, updatedResource.getModuleId());

        return response;
    }

    @Override
    public void deleteResource(Long resourceId) {

        StudentResource resource = studentResourceRepository
                .findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student resource not found with id: " + resourceId));

        Long moduleId = resource.getModuleId();
        studentResourceRepository.delete(resource);

        evictResourceCache(resourceId, moduleId);
    }

    private void evictResourceCache(Long resourceId, Long moduleId) {
        if (resourceId != null) {
            redisCacheService.delete("resource:id:" + resourceId);
        }
        if (moduleId != null) {
            redisCacheService.delete("resource:module:" + moduleId);
            redisCacheService.delete("ai_resource:module:" + moduleId);
            redisCacheService.delete("ai_content:module:" + moduleId);
        }
    }
}