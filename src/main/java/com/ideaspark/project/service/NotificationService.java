package com.ideaspark.project.service;

import com.ideaspark.project.model.dto.request.CreateNotificationRequest;
import com.ideaspark.project.model.dto.response.NotificationResponse;
import com.ideaspark.project.model.entity.Notification;
import com.ideaspark.project.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息通知服务层
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 创建消息通知
     */
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .relatedId(request.getRelatedId())
                .relatedType(request.getRelatedType())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .senderAvatar(request.getSenderAvatar())
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        return convertToResponse(saved);
    }

    /**
     * 获取用户的消息列表
     */
    public Page<NotificationResponse> getUserNotifications(Long userId, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notification> notifications;

        if (type != null && !type.isEmpty() && !"all".equalsIgnoreCase(type)) {
            notifications = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type.toUpperCase(), pageable);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return notifications.map(this::convertToResponse);
    }

    /**
     * 获取用户的未读消息
     */
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户未读消息数量
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 标记消息为已读
     */
    @Transactional
    public boolean markAsRead(Long id, Long userId) {
        int updated = notificationRepository.markAsRead(id, userId);
        return updated > 0;
    }

    /**
     * 标记所有消息为已读
     */
    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId);
    }

    /**
     * 删除已读消息
     */
    @Transactional
    public int deleteReadNotifications(Long userId) {
        return notificationRepository.deleteReadNotifications(userId);
    }

    /**
     * 删除单条消息
     */
    @Transactional
    public boolean deleteNotification(Long id, Long userId) {
        int deleted = notificationRepository.deleteByIdAndUserId(id, userId);
        return deleted > 0;
    }

    /**
     * 转换为响应DTO
     */
    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .relatedId(notification.getRelatedId())
                .relatedType(notification.getRelatedType())
                .senderId(notification.getSenderId())
                .senderName(notification.getSenderName())
                .senderAvatar(notification.getSenderAvatar())
                .createdAt(notification.getCreatedAt())
                .timeAgo(formatTimeAgo(notification.getCreatedAt()))
                .build();
    }

    /**
     * 格式化时间为友好格式
     */
    private String formatTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);

        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 7) {
            return days + "天前";
        } else {
            return createdAt.toLocalDate().toString();
        }
    }
}
