package com.ailearning.learning_resource_service.controller;

import com.ailearning.learning_resource_service.dto.request.CreateStudentResourceRequest;
import com.ailearning.learning_resource_service.dto.request.UpdateStudentResourceRequest;
import com.ailearning.learning_resource_service.dto.response.StudentResourceResponse;
import com.ailearning.learning_resource_service.service.StudentResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Student Resources")
public class StudentResourceController {

    private final StudentResourceService studentResourceService;

    @PostMapping
    @Operation(summary = "Create a student learning resource")
    public ResponseEntity<StudentResourceResponse> createResource(
            @Valid @RequestBody CreateStudentResourceRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentResourceService.createResource(request));
    }

    @GetMapping("/{resourceId}")
    @Operation(summary = "Get a resource by ID")
    public ResponseEntity<StudentResourceResponse> getResource(
            @PathVariable Long resourceId) {

        return ResponseEntity.ok(
                studentResourceService.getResource(resourceId));
    }

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Get all resources for a module")
    public ResponseEntity<List<StudentResourceResponse>> getResourcesByModule(
            @PathVariable Long moduleId) {

        return ResponseEntity.ok(
                studentResourceService
                        .getResourcesByModule(moduleId));
    }

    @PutMapping("/{resourceId}")
    @Operation(
            summary = "Update a student resource",
            description = "Updates an existing student resource"
    )
    public ResponseEntity<StudentResourceResponse> updateResource(
            @PathVariable Long resourceId,
            @Valid @RequestBody UpdateStudentResourceRequest request) {

        return ResponseEntity.ok(
                studentResourceService.updateResource(resourceId, request)
        );
    }

    @DeleteMapping("/{resourceId}")
    @Operation(
            summary = "Delete a student resource",
            description = "Deletes an existing student resource"
    )
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long resourceId) {

        studentResourceService.deleteResource(resourceId);

        return ResponseEntity.noContent().build();
    }
}