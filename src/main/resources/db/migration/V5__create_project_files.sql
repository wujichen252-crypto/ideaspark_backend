CREATE TABLE project_files (
    id VARCHAR(36) PRIMARY KEY COMMENT '文件唯一标识',
    project_id VARCHAR(36) COMMENT '关联项目ID',
    name VARCHAR(100) NOT NULL COMMENT '文件名',
    type VARCHAR(20) COMMENT '文件类型：document/code/image',
    ext VARCHAR(10) COMMENT '文件后缀',
    size BIGINT COMMENT '文件大小(字节)',
    source VARCHAR(20) COMMENT '来源：upload/generated',
    content LONGTEXT COMMENT '文本内容或代码',
    file_url VARCHAR(255) COMMENT 'OSS文件URL',
    created_at TIMESTAMP COMMENT '上传时间',
    updated_at TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_project_files_project_id FOREIGN KEY (project_id) REFERENCES projects(id)
) COMMENT '项目文件表';

CREATE INDEX idx_project_files_project_id ON project_files(project_id);

