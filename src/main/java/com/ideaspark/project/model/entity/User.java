package com.ideaspark.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "position", length = 255)
    private String position;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "per_website", length = 100)
    private String perWebsite;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "is_hide", nullable = false)
    private Boolean isHide;

    @Column(name = "is_notifisys", nullable = false)
    private Boolean isNotifSys;

    @Column(name = "is_notiftrends", nullable = false)
    private Boolean isNotifTrends;

    @Column(name = "is_notifipost", nullable = false)
    private Boolean isNotifPost;

    @Column(name = "likes_count")
    private Integer likesCount;

    @Column(name = "followers_count")
    private Integer followersCount;

    @Column(name = "following_count")
    private Integer followingCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "owner")
    private List<Project> ownedProjects;

    @OneToMany(mappedBy = "user")
    private List<ProjectMember> projectMemberships;

    @OneToMany(mappedBy = "user")
    private List<ChatSession> chatSessions;

    @OneToMany(mappedBy = "author")
    private List<CommunityPost> communityPosts;

    @OneToMany(mappedBy = "user")
    private List<CommunityComment> comments;

    /**
     * 初始化主键与审计字段的默认值
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.role == null || this.role.isBlank()) {
            this.role = "USER";
        }
        if (this.likesCount == null) {
            this.likesCount = 0;
        }
        if (this.followersCount == null) {
            this.followersCount = 0;
        }
        if (this.followingCount == null) {
            this.followingCount = 0;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.isHide == null) {
            this.isHide = false;
        }
        if (this.isNotifSys == null) {
            this.isNotifSys = false;
        }
        if (this.isNotifTrends == null) {
            this.isNotifTrends = false;
        }
        if (this.isNotifPost == null) {
            this.isNotifPost = false;
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

