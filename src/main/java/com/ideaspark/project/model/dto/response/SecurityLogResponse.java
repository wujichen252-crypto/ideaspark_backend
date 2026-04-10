package com.ideaspark.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 安全日志响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityLogResponse {

    private Long id;
    private String actionType;
    private String description;
    private String ipAddress;
    private String location;
    private String device;
    private String status;
    private LocalDateTime createdAt;
    private String timeAgo;
}
