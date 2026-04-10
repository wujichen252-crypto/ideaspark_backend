package com.ideaspark.project.service;

import com.ideaspark.project.model.dto.response.SecurityLogResponse;
import com.ideaspark.project.model.entity.SecurityLog;
import com.ideaspark.project.repository.SecurityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 安全日志服务层
 */
@Service
@RequiredArgsConstructor
public class SecurityLogService {

    private final SecurityLogRepository securityLogRepository;

    /**
     * 获取用户的安全日志列表
     */
    public Page<SecurityLogResponse> getUserSecurityLogs(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SecurityLog> logs = securityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return logs.map(this::convertToResponse);
    }

    /**
     * 创建安全日志
     */
    @Transactional
    public SecurityLogResponse createSecurityLog(Long userId, String actionType, String description,
                                                   String ipAddress, String location, String device,
                                                   String userAgent, String status) {
        SecurityLog log = SecurityLog.builder()
                .userId(userId)
                .actionType(actionType)
                .description(description)
                .ipAddress(ipAddress)
                .location(location)
                .device(device)
                .userAgent(userAgent)
                .status(status)
                .build();

        SecurityLog saved = securityLogRepository.save(log);
        return convertToResponse(saved);
    }

    /**
     * 记录登录成功日志
     */
    @Transactional
    public void recordLoginSuccess(Long userId, String ipAddress, String device, String userAgent) {
        createSecurityLog(userId, "LOGIN", "使用密码登录成功", 
                ipAddress, "未知", device, userAgent, "SUCCESS");
    }

    /**
     * 记录密码修改日志
     */
    @Transactional
    public void recordPasswordChange(Long userId, String ipAddress) {
        createSecurityLog(userId, "PASSWORD_CHANGE", "账户密码已修改成功",
                ipAddress, null, null, null, "SUCCESS");
    }

    /**
     * 记录资料更新日志
     */
    @Transactional
    public void recordProfileUpdate(Long userId, String ipAddress) {
        createSecurityLog(userId, "PROFILE_UPDATE", "修改了个人简介和头像",
                ipAddress, null, null, null, "SUCCESS");
    }

    /**
     * 转换为响应DTO
     */
    private SecurityLogResponse convertToResponse(SecurityLog log) {
        return SecurityLogResponse.builder()
                .id(log.getId())
                .actionType(log.getActionType())
                .description(log.getDescription())
                .ipAddress(log.getIpAddress())
                .location(log.getLocation())
                .device(log.getDevice())
                .status(log.getStatus())
                .createdAt(log.getCreatedAt())
                .timeAgo(formatTimeAgo(log.getCreatedAt()))
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
