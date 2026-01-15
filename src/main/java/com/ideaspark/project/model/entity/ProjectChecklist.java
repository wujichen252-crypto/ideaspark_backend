package com.ideaspark.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "project_checklists")
public class ProjectChecklist {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "module_key", length = 30)
    private String moduleKey;

    @Column(name = "label", nullable = false, length = 255)
    private String label;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "priority", length = 10)
    private String priority;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "assignee_user_id", length = 36)
    private String assigneeUserId;

    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 初始化主键与默认字段
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.isCompleted == null) {
            this.isCompleted = false;
        }
        if (this.priority == null || this.priority.isBlank()) {
            this.priority = "medium";
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 更新审计字段
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
