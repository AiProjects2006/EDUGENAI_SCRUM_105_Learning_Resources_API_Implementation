package com.ailearning.learning_resource_service.entity;

import com.ailearning.learning_resource_service.enums.ResourceStatus;
import com.ailearning.learning_resource_service.enums.ResourceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_resource")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "external_file_id")
    private String externalFileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider")
    private com.ailearning.learning_resource_service.enums.StorageProvider storageProvider;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status;

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
            status = ResourceStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}