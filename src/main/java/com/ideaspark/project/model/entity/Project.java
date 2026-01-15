package com.ideaspark.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Project parent;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "detailed_description", columnDefinition = "TEXT")
    private String detailedDescription;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "cover_url", length = 255)
    private String coverUrl;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "current_module", length = 30)
    private String currentModule;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "progress")
    private Integer progress;

    @Column(name = "visibility", length = 10)
    private String visibility;

    @Column(name = "allow_fork")
    private Boolean allowFork;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "project")
    private List<ProjectMember> members;

    @OneToMany(mappedBy = "project")
    private List<ProjectModule> modules;

    @OneToMany(mappedBy = "project")
    private List<ProjectFile> files;

    @OneToMany(mappedBy = "project")
    private List<ProjectChecklist> checklists;

    @OneToMany(mappedBy = "project")
    private List<ChatSession> chatSessions;

    @OneToMany(mappedBy = "project")
    private List<CommunityPost> communityPosts;

    /**
     * 初始化主键与默认字段
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "draft";
        }
        if (this.progress == null) {
            this.progress = 0;
        }
        if (this.visibility == null || this.visibility.isBlank()) {
            this.visibility = "private";
        }
        if (this.allowFork == null) {
            this.allowFork = true;
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
