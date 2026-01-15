CREATE TABLE community_posts (
    id VARCHAR(36) PRIMARY KEY COMMENT '帖子ID',
    author_id VARCHAR(36) COMMENT '发布者ID',
    project_id VARCHAR(36) COMMENT '关联项目ID(可选)',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容(支持Markdown)',
    tags VARCHAR(255) COMMENT '标签(逗号分隔)',
    likes_count INT DEFAULT 0 COMMENT '点赞数',
    views_count INT DEFAULT 0 COMMENT '浏览数',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    created_at TIMESTAMP COMMENT '发布时间',
    updated_at TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_community_posts_author_id FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_community_posts_project_id FOREIGN KEY (project_id) REFERENCES projects(id)
) COMMENT '社区帖子表';

CREATE INDEX idx_community_posts_author_id ON community_posts(author_id);
CREATE INDEX idx_community_posts_project_id ON community_posts(project_id);

