CREATE TABLE project_members (
    id VARCHAR(36) PRIMARY KEY COMMENT '成员记录ID',
    project_id VARCHAR(36) COMMENT '关联项目ID',
    user_id BIGINT COMMENT '关联用户ID',
    member_role VARCHAR(20) NOT NULL COMMENT '成员角色：owner/admin/editor/viewer',
    joined_at TIMESTAMP COMMENT '加入时间',
    CONSTRAINT fk_project_members_project_id FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_project_members_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT '项目成员关联表';

CREATE INDEX idx_project_members_project_id ON project_members(project_id);
CREATE INDEX idx_project_members_user_id ON project_members(user_id);

