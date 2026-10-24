package com.ailearning.learning_resource_service.service;

import com.ailearning.learning_resource_service.dto.request.CreateStudentResourceRequest;
import com.ailearning.learning_resource_service.dto.request.UpdateStudentResourceRequest;
import com.ailearning.learning_resource_service.dto.response.StudentResourceResponse;

import java.util.List;

public interface StudentResourceService {

    StudentResourceResponse createResource(
            CreateStudentResourceRequest request
    );

    StudentResourceResponse getResource(Long resourceId);

    List<StudentResourceResponse> getResourcesByModule(Long moduleId);

    StudentResourceResponse updateResource(
            Long resourceId,
            UpdateStudentResourceRequest request
    );

    void deleteResource(Long resourceId);
}