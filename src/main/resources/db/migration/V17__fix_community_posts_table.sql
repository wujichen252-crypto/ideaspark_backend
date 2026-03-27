-- 修复 community_posts 表结构，添加缺失字段
-- 使用标准SQL语法，兼容MySQL和H2

-- 添加 group_id 列（如果不存在）
ALTER TABLE community_posts ADD COLUMN IF NOT EXISTS group_id VARCHAR(36) NULL;

-- 添加 images 列（如果不存在）
ALTER TABLE community_posts ADD COLUMN IF NOT EXISTS images VARCHAR(2000);

-- 添加 channel 列（如果不存在）
ALTER TABLE community_posts ADD COLUMN IF NOT EXISTS channel VARCHAR(20);

-- 添加 visibility 列（如果不存在）
ALTER TABLE community_posts ADD COLUMN IF NOT EXISTS visibility VARCHAR(20) DEFAULT 'public';

-- 添加 comments_count 列（如果不存在）
ALTER TABLE community_posts ADD COLUMN IF NOT EXISTS comments_count INT DEFAULT 0;

-- 添加 group_id 索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_community_posts_group_id ON community_posts(group_id);
