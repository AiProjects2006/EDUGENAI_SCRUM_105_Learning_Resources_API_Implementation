package com.ailearning.learning_resource_service.repository;

import com.ailearning.learning_resource_service.entity.AIResource;
import com.ailearning.learning_resource_service.enums.AIResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIResourceRepository
        extends JpaRepository<AIResource, Long> {

    List<AIResource> findByModuleId(Long moduleId);

    List<AIResource> findByModuleIdAndStatus(
            Long moduleId,
            AIResourceStatus status
    );
}