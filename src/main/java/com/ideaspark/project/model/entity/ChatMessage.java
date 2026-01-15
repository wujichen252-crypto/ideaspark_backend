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
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private ChatSession session;

    @Column(name = "role", nullable = false, length = 10)
    private String role;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "message_type", length = 10)
    private String messageType;

    @Column(name = "language", length = 20)
    private String language;

    @Column(name = "image_urls", columnDefinition = "JSON")
    private String imageUrls;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 初始化主键与创建时间
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
