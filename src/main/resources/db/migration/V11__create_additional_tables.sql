CREATE TABLE teams (
    id VARCHAR(36) PRIMARY KEY COMMENT '团队唯一标识',
    owner_id BIGINT NOT NULL COMMENT '团队所有者',
    name VARCHAR(100) NOT NULL COMMENT '团队名称',
    is_personal BOOLEAN DEFAULT FALSE COMMENT '是否个人空间',
    avatar_url VARCHAR(255) COMMENT '团队头像',
    description TEXT COMMENT '团队描述',
    dissolved_at TIMESTAMP NULL COMMENT '解散时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_teams_owner_id FOREIGN KEY (owner_id) REFERENCES users(id),
    UNIQUE KEY uk_teams_owner_personal (owner_id, is_personal)
) COMMENT '团队/空间表';

CREATE INDEX idx_teams_owner_id ON teams(owner_id);

CREATE TABLE team_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录唯一标识',
    team_id VARCHAR(36) NOT NULL COMMENT '团队 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    role VARCHAR(20) NOT NULL COMMENT '角色 (owner, admin, member, visitor)',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    CONSTRAINT fk_team_members_team_id FOREIGN KEY (team_id) REFERENCES teams(id),
    CONSTRAINT fk_team_members_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_team_members_team_user (team_id, user_id)
) COMMENT '团队成员表';

CREATE INDEX idx_team_members_team_id ON team_members(team_id);
CREATE INDEX idx_team_members_user_id ON team_members(user_id);

CREATE TABLE team_resources (
    id VARCHAR(36) PRIMARY KEY COMMENT '资源库唯一标识',
    team_id VARCHAR(36) NOT NULL COMMENT '所属团队 ID',
    kind VARCHAR(20) NOT NULL COMMENT '类型 (repo, fonts)',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    item_count INT DEFAULT 0 COMMENT '资源条目数',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_team_resources_team_id FOREIGN KEY (team_id) REFERENCES teams(id)
) COMMENT '团队资源库表';

CREATE INDEX idx_team_resources_team_id ON team_resources(team_id);

CREATE TABLE plugins (
    id VARCHAR(36) PRIMARY KEY COMMENT '插件唯一标识',
    `key` VARCHAR(50) NOT NULL UNIQUE COMMENT '插件 Key',
    name VARCHAR(100) NOT NULL COMMENT '插件名称',
    category VARCHAR(20) COMMENT '分类',
    description TEXT COMMENT '简介',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '插件表';

CREATE TABLE project_plugins (
    id VARCHAR(36) PRIMARY KEY COMMENT '记录唯一标识',
    project_id VARCHAR(36) NOT NULL COMMENT '项目 ID',
    plugin_id VARCHAR(36) NOT NULL COMMENT '插件 ID',
    sort_order INT DEFAULT 0 COMMENT '展示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_project_plugins_project_id FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_project_plugins_plugin_id FOREIGN KEY (plugin_id) REFERENCES plugins(id),
    UNIQUE KEY uk_project_plugins_project_plugin (project_id, plugin_id)
) COMMENT '项目插件关联表';

CREATE INDEX idx_project_plugins_project_id ON project_plugins(project_id);
CREATE INDEX idx_project_plugins_plugin_id ON project_plugins(plugin_id);

CREATE TABLE community_groups (
    id VARCHAR(36) PRIMARY KEY COMMENT '圈子唯一标识',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '圈子名称',
    keyword VARCHAR(50) COMMENT '匹配关键字',
    icon_url VARCHAR(255) COMMENT '图标 URL',
    cover_url VARCHAR(255) COMMENT '封面 URL',
    description TEXT COMMENT '圈子描述',
    created_by BIGINT NULL COMMENT '创建者',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_community_groups_created_by FOREIGN KEY (created_by) REFERENCES users(id)
) COMMENT '社区圈子表';

CREATE INDEX idx_community_groups_created_by ON community_groups(created_by);

CREATE TABLE community_group_members (
    id VARCHAR(36) PRIMARY KEY COMMENT '记录唯一标识',
    group_id VARCHAR(36) NOT NULL COMMENT '圈子 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    role VARCHAR(20) DEFAULT 'member' COMMENT '圈子角色',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    CONSTRAINT fk_community_group_members_group_id FOREIGN KEY (group_id) REFERENCES community_groups(id),
    CONSTRAINT fk_community_group_members_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_community_group_members_group_user (group_id, user_id)
) COMMENT '圈子成员表';

CREATE INDEX idx_community_group_members_group_id ON community_group_members(group_id);
CREATE INDEX idx_community_group_members_user_id ON community_group_members(user_id);

CREATE TABLE user_follows (
    id VARCHAR(36) PRIMARY KEY COMMENT '关注记录唯一标识',
    follower_id BIGINT NOT NULL COMMENT '关注者用户 ID',
    following_id BIGINT NOT NULL COMMENT '被关注者用户 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    CONSTRAINT fk_user_follows_follower_id FOREIGN KEY (follower_id) REFERENCES users(id),
    CONSTRAINT fk_user_follows_following_id FOREIGN KEY (following_id) REFERENCES users(id),
    CONSTRAINT ck_user_follows_not_self CHECK (follower_id <> following_id),
    UNIQUE KEY uk_user_follows_pair (follower_id, following_id)
) COMMENT '用户关注关系表';

CREATE INDEX idx_user_follows_follower_id ON user_follows(follower_id);
CREATE INDEX idx_user_follows_following_id ON user_follows(following_id);

