package com.ideaspark.project.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 安全日志实体类
 * 记录用户登录、密码修改等安全相关操作
 */
@Entity
@Table(name = "security_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 操作类型: LOGIN(登录), LOGOUT(登出), PASSWORD_CHANGE(修改密码),
     * PROFILE_UPDATE(更新资料), PASSWORD_RESET(重置密码), ABNORMAL_LOGIN(异常登录)
     */
    @Column(name = "action_type", nullable = false, length = 30)
    private String actionType;

    /**
     * 操作描述
     */
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    /**
     * 登录IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 登录地点
     */
    @Column(name = "location", length = 100)
    private String location;

    /**
     * 设备信息
     */
    @Column(name = "device", length = 200)
    private String device;

    /**
     * 浏览器/客户端信息
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 状态: SUCCESS(成功), FAILED(失败)
     */
    @Column(name = "status", length = 20)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
