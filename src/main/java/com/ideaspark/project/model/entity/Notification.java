package com.ideaspark.project.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 消息通知实体类
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 接收用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 消息类型: SYSTEM(系统), COMMENT(评论), LIKE(点赞), FOLLOW(关注), PROJECT(项目)
     */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    /**
     * 消息标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 消息内容
     */
    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    /**
     * 是否已读
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /**
     * 关联对象ID（如项目ID、帖子ID等）
     */
    @Column(name = "related_id", length = 50)
    private String relatedId;

    /**
     * 关联对象类型
     */
    @Column(name = "related_type", length = 20)
    private String relatedType;

    /**
     * 发送者ID（系统消息为null）
     */
    @Column(name = "sender_id")
    private Long senderId;

    /**
     * 发送者名称
     */
    @Column(name = "sender_name", length = 50)
    private String senderName;

    /**
     * 发送者头像
     */
    @Column(name = "sender_avatar", length = 500)
    private String senderAvatar;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
