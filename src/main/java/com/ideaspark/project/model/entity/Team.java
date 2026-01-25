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
@Table(name = "teams")
public class Team {

    @Id
    @Column(name = "uuiu", length = 36)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "is_personal")
    private Boolean isPersonal;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "dissolved_at")
    private LocalDateTime dissolvedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 初始化主键与审计字段的默认值
     */
    @PrePersist
    public void prePersist() {
        if (this.uuid == null || this.uuid.isBlank()) {
            int value = (int) (Math.random() * 1_000_000);
            this.uuid = String.format("%06d", value);
        }
        if (this.isPersonal == null) {
            this.isPersonal = false;
        }
        if (this.teamSize == null) {
            this.teamSize = 0;
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

