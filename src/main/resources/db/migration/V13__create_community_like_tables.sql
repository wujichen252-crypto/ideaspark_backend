CREATE TABLE community_post_likes (
    id VARCHAR(36) PRIMARY KEY COMMENT '记录唯一标识',
    post_id VARCHAR(36) NOT NULL COMMENT '帖子 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    CONSTRAINT fk_community_post_likes_post_id FOREIGN KEY (post_id) REFERENCES community_posts(id),
    CONSTRAINT fk_community_post_likes_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_community_post_likes_post_user (post_id, user_id)
) COMMENT '帖子点赞表';

CREATE INDEX idx_community_post_likes_post_id ON community_post_likes(post_id);
CREATE INDEX idx_community_post_likes_user_id ON community_post_likes(user_id);

CREATE TABLE community_comment_likes (
    id VARCHAR(36) PRIMARY KEY COMMENT '记录唯一标识',
    comment_id VARCHAR(36) NOT NULL COMMENT '评论 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    CONSTRAINT fk_community_comment_likes_comment_id FOREIGN KEY (comment_id) REFERENCES community_comments(id),
    CONSTRAINT fk_community_comment_likes_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_community_comment_likes_comment_user (comment_id, user_id)
) COMMENT '评论点赞表';

CREATE INDEX idx_community_comment_likes_comment_id ON community_comment_likes(comment_id);
CREATE INDEX idx_community_comment_likes_user_id ON community_comment_likes(user_id);

