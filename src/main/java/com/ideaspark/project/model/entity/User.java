package com.ideaspark.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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
    private Boolean isHide = true;

    @Column(name = "is_notifisys", nullable = false)
    private Boolean isNotifSys = true;

    @Column(name = "is_notiftrends", nullable = false)
    private Boolean isNotifTrends = true;

    @Column(name = "is_notifipost", nullable = false)
    private Boolean isNotifPost = false;

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

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPerWebsite() {
        return perWebsite;
    }

    public void setPerWebsite(String perWebsite) {
        this.perWebsite = perWebsite;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsHide() {
        return isHide;
    }

    public void setIsHide(Boolean isHide) {
        this.isHide = isHide;
    }

    public Boolean getIsNotifSys() {
        return isNotifSys;
    }

    public void setIsNotifSys(Boolean isNotifSys) {
        this.isNotifSys = isNotifSys;
    }

    public Boolean getIsNotifTrends() {
        return isNotifTrends;
    }

    public void setIsNotifTrends(Boolean isNotifTrends) {
        this.isNotifTrends = isNotifTrends;
    }

    public Boolean getIsNotifPost() {
        return isNotifPost;
    }

    public void setIsNotifPost(Boolean isNotifPost) {
        this.isNotifPost = isNotifPost;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Project> getOwnedProjects() {
        return ownedProjects;
    }

    public void setOwnedProjects(List<Project> ownedProjects) {
        this.ownedProjects = ownedProjects;
    }

    public List<ProjectMember> getProjectMemberships() {
        return projectMemberships;
    }

    public void setProjectMemberships(List<ProjectMember> projectMemberships) {
        this.projectMemberships = projectMemberships;
    }

    public List<ChatSession> getChatSessions() {
        return chatSessions;
    }

    public void setChatSessions(List<ChatSession> chatSessions) {
        this.chatSessions = chatSessions;
    }

    public List<CommunityPost> getCommunityPosts() {
        return communityPosts;
    }

    public void setCommunityPosts(List<CommunityPost> communityPosts) {
        this.communityPosts = communityPosts;
    }

    public List<CommunityComment> getComments() {
        return comments;
    }

    public void setComments(List<CommunityComment> comments) {
        this.comments = comments;
    }

    /**
     * 初始化主键与审计字段的默认值
     */
    @PrePersist
    public void prePersist() {
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
