package com.ideaspark.project.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建消息通知请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {

    @NotNull(message = "接收用户ID不能为空")
    private Long userId;

    @NotBlank(message = "消息类型不能为空")
    private String type;  // SYSTEM, COMMENT, LIKE, FOLLOW, PROJECT

    @NotBlank(message = "消息标题不能为空")
    private String title;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    private String relatedId;
    private String relatedType;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
}
