package com.ailearning.learning_resource_service.repository;

import com.ailearning.learning_resource_service.entity.AIResourceChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIResourceChunkRepository
        extends JpaRepository<AIResourceChunk, Long> {

    List<AIResourceChunk> findByAiResourceAiResourceIdOrderByChunkIndex(
            Long aiResourceId
    );

    void deleteByAiResourceAiResourceId(Long aiResourceId);

}