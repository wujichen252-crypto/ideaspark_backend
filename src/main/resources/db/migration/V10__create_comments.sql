CREATE TABLE comments (
    id VARCHAR(36) PRIMARY KEY COMMENT '评论ID',
    post_id VARCHAR(36) COMMENT '关联帖子ID',
    author_id BIGINT COMMENT '评论者ID',
    parent_id VARCHAR(36) COMMENT '父评论ID(回复)',
    content TEXT NOT NULL COMMENT '评论内容',
    created_at TIMESTAMP COMMENT '评论时间',
    CONSTRAINT fk_comments_post_id FOREIGN KEY (post_id) REFERENCES community_posts(id),
    CONSTRAINT fk_comments_author_id FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_comments_parent_id FOREIGN KEY (parent_id) REFERENCES comments(id)
) COMMENT '评论表';

CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_author_id ON comments(author_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_id);

