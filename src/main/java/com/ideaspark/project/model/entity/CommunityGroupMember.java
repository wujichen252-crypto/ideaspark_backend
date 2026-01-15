package com.ideaspark.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "community_group_members")
public class CommunityGroupMember {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private CommunityGroup group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    /**
     * 初始化主键与默认字段
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.role == null || this.role.isBlank()) {
            this.role = "member";
        }
        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
    }
}

