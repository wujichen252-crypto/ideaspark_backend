CREATE TABLE project_checklists (
    id VARCHAR(36) PRIMARY KEY COMMENT '任务ID',
    project_id VARCHAR(36) COMMENT '关联项目ID',
    module_key VARCHAR(30) COMMENT '所属模块',
    label VARCHAR(255) NOT NULL COMMENT '任务内容描述',
    is_completed BOOLEAN DEFAULT FALSE COMMENT '是否完成',
    priority VARCHAR(10) DEFAULT 'medium' COMMENT '优先级：high/medium/low',
    due_date TIMESTAMP COMMENT '截止日期',
    created_at TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_project_checklists_project_id FOREIGN KEY (project_id) REFERENCES projects(id)
) COMMENT '项目任务清单表';

CREATE INDEX idx_project_checklists_project_id ON project_checklists(project_id);

