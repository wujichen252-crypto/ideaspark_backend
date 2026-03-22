-- 修复 community_posts 表结构，添加缺失字段
-- 检查并添加缺失的列

-- 添加 group_id 列（如果不存在）
SET @exist_col := (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'community_posts' AND COLUMN_NAME = 'group_id');
SET @sql := IF(@exist_col = 0, 
    'ALTER TABLE community_posts ADD COLUMN group_id VARCHAR(36) NULL COMMENT ''圈子ID''', 
    'SELECT ''group_id column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 images 列（如果不存在）
SET @exist_col := (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'community_posts' AND COLUMN_NAME = 'images');
SET @sql := IF(@exist_col = 0, 
    'ALTER TABLE community_posts ADD COLUMN images JSON COMMENT ''图片URL列表''', 
    'SELECT ''images column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 channel 列（如果不存在）
SET @exist_col := (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'community_posts' AND COLUMN_NAME = 'channel');
SET @sql := IF(@exist_col = 0, 
    'ALTER TABLE community_posts ADD COLUMN channel VARCHAR(20) COMMENT ''频道分类''', 
    'SELECT ''channel column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 visibility 列（如果不存在）
SET @exist_col := (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'community_posts' AND COLUMN_NAME = 'visibility');
SET @sql := IF(@exist_col = 0, 
    'ALTER TABLE community_posts ADD COLUMN visibility VARCHAR(20) DEFAULT ''public'' COMMENT ''可见性''', 
    'SELECT ''visibility column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 comments_count 列（如果不存在）
SET @exist_col := (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'community_posts' AND COLUMN_NAME = 'comments_count');
SET @sql := IF(@exist_col = 0, 
    'ALTER TABLE community_posts ADD COLUMN comments_count INT DEFAULT 0 COMMENT ''评论数''', 
    'SELECT ''comments_count column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 修改 tags 列类型为 JSON（如果当前不是JSON类型）
SET @col_type := (SELECT DATA_TYPE FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'community_posts' AND COLUMN_NAME = 'tags');
SET @sql := IF(@col_type != 'json', 
    'ALTER TABLE community_posts MODIFY tags JSON COMMENT ''标签列表''', 
    'SELECT ''tags column already JSON type''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 group_id 外键约束（如果不存在）
SET @exist_fk := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'community_posts' 
    AND CONSTRAINT_NAME = 'fk_community_posts_group_id' AND CONSTRAINT_TYPE = 'FOREIGN KEY');
SET @sql := IF(@exist_fk = 0, 
    'ALTER TABLE community_posts ADD CONSTRAINT fk_community_posts_group_id FOREIGN KEY (group_id) REFERENCES community_groups(id)', 
    'SELECT ''fk_community_posts_group_id already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 group_id 索引（如果不存在）
SET @exist_idx := (SELECT COUNT(*) FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'community_posts' AND INDEX_NAME = 'idx_community_posts_group_id');
SET @sql := IF(@exist_idx = 0, 
    'CREATE INDEX idx_community_posts_group_id ON community_posts(group_id)', 
    'SELECT ''idx_community_posts_group_id already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
