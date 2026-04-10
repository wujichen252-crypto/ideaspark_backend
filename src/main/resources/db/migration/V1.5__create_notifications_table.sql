-- 创建消息通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    type VARCHAR(20) NOT NULL COMMENT '消息类型: SYSTEM(系统), COMMENT(评论), LIKE(点赞), FOLLOW(关注), PROJECT(项目)',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content VARCHAR(1000) NOT NULL COMMENT '消息内容',
    is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
    related_id VARCHAR(50) DEFAULT NULL COMMENT '关联对象ID（如项目ID、帖子ID等）',
    related_type VARCHAR(20) DEFAULT NULL COMMENT '关联对象类型',
    sender_id BIGINT DEFAULT NULL COMMENT '发送者ID（系统消息为null）',
    sender_name VARCHAR(50) DEFAULT NULL COMMENT '发送者名称',
    sender_avatar VARCHAR(500) DEFAULT NULL COMMENT '发送者头像',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_user_id_created_at (user_id, created_at DESC),
    INDEX idx_user_id_is_read (user_id, is_read),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';
