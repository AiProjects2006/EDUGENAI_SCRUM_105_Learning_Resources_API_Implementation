package com.ailearning.learning_resource_service.repository;

import com.ailearning.learning_resource_service.entity.StudentResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentResourceRepository
        extends JpaRepository<StudentResource, Long> {

    List<StudentResource> findByModuleId(Long moduleId);
}