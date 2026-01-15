CREATE TABLE project_modules (
    id VARCHAR(36) PRIMARY KEY COMMENT '模块记录ID',
    project_id VARCHAR(36) COMMENT '关联项目ID',
    module_key VARCHAR(30) NOT NULL COMMENT '模块标识：idea/planning/design等',
    module_data JSON COMMENT '模块详细数据(JSON格式)',
    updated_at TIMESTAMP COMMENT '最后更新时间',
    CONSTRAINT fk_project_modules_project_id FOREIGN KEY (project_id) REFERENCES projects(id)
) COMMENT '项目模块数据表';

CREATE INDEX idx_project_modules_project_id ON project_modules(project_id);

