CREATE TABLE chat_messages (
    id VARCHAR(36) PRIMARY KEY COMMENT '消息ID',
    session_id VARCHAR(36) COMMENT '所属会话ID',
    role VARCHAR(10) NOT NULL COMMENT '角色：user/assistant/system',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type VARCHAR(10) COMMENT '类型：text/code/image',
    language VARCHAR(20) COMMENT '代码语言(如果是代码)',
    metadata JSON COMMENT '额外元数据',
    created_at TIMESTAMP COMMENT '发送时间',
    CONSTRAINT fk_chat_messages_session_id FOREIGN KEY (session_id) REFERENCES chat_sessions(id)
) COMMENT '对话消息记录表';

CREATE INDEX idx_chat_messages_session_id ON chat_messages(session_id);

