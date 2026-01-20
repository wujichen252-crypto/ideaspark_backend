CREATE TABLE projects (
    id VARCHAR(36) PRIMARY KEY COMMENT '项目唯一标识',
    owner_id BIGINT COMMENT '项目所有者ID',
    name VARCHAR(100) NOT NULL COMMENT '项目名称',
    description TEXT COMMENT '项目简述',
    detailed_description TEXT COMMENT '详细描述',
    category VARCHAR(50) COMMENT '分类：web/mobile/ai等',
    cover_url VARCHAR(255) COMMENT '封面图URL',
    current_module VARCHAR(30) COMMENT '当前进行中的模块',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft/active/completed/archived',
    progress INT DEFAULT 0 COMMENT '总体进度(0-100)',
    visibility VARCHAR(10) DEFAULT 'private' COMMENT '可见性：public/private',
    allow_fork BOOLEAN DEFAULT TRUE COMMENT '是否允许Fork',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '逻辑删除标记',
    created_at TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_projects_owner_id FOREIGN KEY (owner_id) REFERENCES users(id)
) COMMENT '项目主表';

CREATE INDEX idx_projects_owner_id ON projects(owner_id);

