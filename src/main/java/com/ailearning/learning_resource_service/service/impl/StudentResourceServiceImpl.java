package com.ailearning.learning_resource_service.service.impl;

import com.ailearning.learning_resource_service.dto.request.CreateStudentResourceRequest;
import com.ailearning.learning_resource_service.dto.request.UpdateStudentResourceRequest;
import com.ailearning.learning_resource_service.dto.response.StudentResourceResponse;
import com.ailearning.learning_resource_service.entity.StudentResource;
import com.ailearning.learning_resource_service.exception.ResourceNotFoundException;
import com.ailearning.learning_resource_service.repository.StudentResourceRepository;
import com.ailearning.learning_resource_service.service.StudentResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentResourceServiceImpl implements StudentResourceService {

    private final StudentResourceRepository studentResourceRepository;

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

        StudentResource savedResource =
                studentResourceRepository.save(resource);

        return mapToResponse(savedResource);
    }

    @Override
    public List<StudentResourceResponse> getResourcesByModule(
            Long moduleId) {

        return studentResourceRepository
                .findByModuleId(moduleId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public StudentResourceResponse getResource(Long resourceId) {

        StudentResource resource = studentResourceRepository
                .findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student resource not found with id: " + resourceId
                        )
                );

        return mapToResponse(resource);
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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student resource not found with id: " + resourceId
                        )
                );

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

        StudentResource updatedResource =
                studentResourceRepository.save(resource);

        return mapToResponse(updatedResource);
    }

    @Override
    public void deleteResource(Long resourceId) {

        StudentResource resource = studentResourceRepository
                .findById(resourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student resource not found with id: " + resourceId
                        )
                );

        studentResourceRepository.delete(resource);
    }
}