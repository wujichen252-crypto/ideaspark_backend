-- 创建安全日志表
CREATE TABLE IF NOT EXISTS security_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    action_type VARCHAR(30) NOT NULL COMMENT '操作类型: LOGIN(登录), LOGOUT(登出), PASSWORD_CHANGE(修改密码), PROFILE_UPDATE(更新资料), PASSWORD_RESET(重置密码), ABNORMAL_LOGIN(异常登录)',
    description VARCHAR(500) NOT NULL COMMENT '操作描述',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT '登录IP地址',
    location VARCHAR(100) DEFAULT NULL COMMENT '登录地点',
    device VARCHAR(200) DEFAULT NULL COMMENT '设备信息',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '浏览器/客户端信息',
    status VARCHAR(20) DEFAULT NULL COMMENT '状态: SUCCESS(成功), FAILED(失败)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_user_id_created_at (user_id, created_at DESC),
    INDEX idx_action_type (action_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='安全日志表';
