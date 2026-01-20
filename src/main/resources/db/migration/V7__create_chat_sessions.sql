CREATE TABLE chat_sessions (
    id VARCHAR(36) PRIMARY KEY COMMENT '会话ID',
    user_id BIGINT COMMENT '创建用户ID',
    project_id VARCHAR(36) COMMENT '关联项目ID(可选)',
    title VARCHAR(100) COMMENT '会话标题',
    model VARCHAR(50) COMMENT '使用的AI模型',
    system_prompt TEXT COMMENT '系统预设提示词',
    created_at TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP COMMENT '最后活动时间',
    CONSTRAINT fk_chat_sessions_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_chat_sessions_project_id FOREIGN KEY (project_id) REFERENCES projects(id)
) COMMENT 'AI对话会话表';

CREATE INDEX idx_chat_sessions_user_id ON chat_sessions(user_id);
CREATE INDEX idx_chat_sessions_project_id ON chat_sessions(project_id);

