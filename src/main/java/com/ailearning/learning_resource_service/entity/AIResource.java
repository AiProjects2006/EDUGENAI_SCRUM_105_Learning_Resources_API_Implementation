package com.ailearning.learning_resource_service.entity;

import com.ailearning.learning_resource_service.enums.AIResourceStatus;
import com.ailearning.learning_resource_service.enums.AISourceType;
import com.ailearning.learning_resource_service.enums.StorageProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_resource")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_resource_id")
    private Long aiResourceId;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private AISourceType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider")
    private StorageProvider storageProvider;

    @Column(name = "external_file_id")
    private String externalFileId;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "mime_type")
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AIResourceStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = AIResourceStatus.PROCESSING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}