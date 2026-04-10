package com.ideaspark.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息通知响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String type;
    private String title;
    private String content;
    private Boolean isRead;
    private String relatedId;
    private String relatedType;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private LocalDateTime createdAt;
    private String timeAgo;  // 友好时间格式，如"5分钟前"
}
