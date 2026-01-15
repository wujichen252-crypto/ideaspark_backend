CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY COMMENT '用户唯一标识',
    username VARCHAR(50) NOT NULL COMMENT '用户昵称',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '登录邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '加密密码',
    avatar VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(50) DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    bio TEXT COMMENT '个人简介',
    likes_count INT DEFAULT 0 COMMENT '获赞总数',
    followers_count INT DEFAULT 0 COMMENT '粉丝数',
    following_count INT DEFAULT 0 COMMENT '关注数',
    state CHAR(1) DEFAULT 'N' COMMENT '状态：N-正常，L-锁定，X-删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户信息表';

