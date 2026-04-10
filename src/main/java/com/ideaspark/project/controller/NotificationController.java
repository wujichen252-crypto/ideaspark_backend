package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.request.CreateNotificationRequest;
import com.ideaspark.project.model.dto.response.NotificationResponse;
import com.ideaspark.project.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息通知控制器
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "消息通知", description = "消息通知相关接口")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取用户的消息列表
     */
    @GetMapping
    @Operation(summary = "获取消息列表", description = "分页获取当前用户的消息通知列表")
    public ResponseEntity<?> getNotifications(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "消息类型过滤")
            @RequestParam(required = false) String type,
            @Parameter(description = "页码，默认1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量，默认20")
            @RequestParam(defaultValue = "20") int size) {
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, type, page, size);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", Map.of(
                        "notifications", notifications.getContent(),
                        "total", notifications.getTotalElements(),
                        "page", page,
                        "size", size,
                        "totalPages", notifications.getTotalPages()
                )
        ));
    }

    /**
     * 获取未读消息列表
     */
    @GetMapping("/unread")
    @Operation(summary = "获取未读消息", description = "获取当前用户的所有未读消息")
    public ResponseEntity<?> getUnreadNotifications(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", notifications
        ));
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    @Operation(summary = "获取未读消息数量", description = "获取当前用户的未读消息数量")
    public ResponseEntity<?> getUnreadCount(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", Map.of("count", count)
        ));
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{id}/read")
    @Operation(summary = "标记已读", description = "将指定消息标记为已读")
    public ResponseEntity<?> markAsRead(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "消息ID")
            @PathVariable Long id) {
        boolean success = notificationService.markAsRead(id, userId);
        if (success) {
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "标记已读成功"
            ));
        } else {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "message", "消息不存在"
            ));
        }
    }

    /**
     * 标记所有消息为已读
     */
    @PutMapping("/read/all")
    @Operation(summary = "全部标记已读", description = "将当前用户的所有未读消息标记为已读")
    public ResponseEntity<?> markAllAsRead(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId) {
        int count = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "已将" + count + "条消息标记为已读"
        ));
    }

    /**
     * 删除已读消息
     */
    @DeleteMapping("/read")
    @Operation(summary = "删除已读消息", description = "删除当前用户的所有已读消息")
    public ResponseEntity<?> deleteReadNotifications(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId) {
        int count = notificationService.deleteReadNotifications(userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "已删除" + count + "条已读消息"
        ));
    }

    /**
     * 删除单条消息
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除消息", description = "删除指定消息")
    public ResponseEntity<?> deleteNotification(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "消息ID")
            @PathVariable Long id) {
        boolean success = notificationService.deleteNotification(id, userId);
        if (success) {
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "删除成功"
            ));
        } else {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "message", "消息不存在"
            ));
        }
    }

    /**
     * 创建消息（内部使用，用于其他服务调用）
     */
    @PostMapping
    @Operation(summary = "创建消息", description = "创建新的消息通知（内部使用）")
    public ResponseEntity<?> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        NotificationResponse notification = notificationService.createNotification(request);
        return ResponseEntity.status(201).body(Map.of(
                "status", 201,
                "message", "创建成功",
                "data", notification
        ));
    }
}
