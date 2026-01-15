ALTER TABLE users
    DROP COLUMN state;

ALTER TABLE projects
    ADD COLUMN team_id VARCHAR(36) NULL;

ALTER TABLE projects
    ADD COLUMN parent_id VARCHAR(36) NULL;

ALTER TABLE projects
    ADD COLUMN type VARCHAR(20) NULL;

ALTER TABLE projects
    DROP COLUMN is_deleted;

ALTER TABLE projects
    ADD CONSTRAINT fk_projects_team_id FOREIGN KEY (team_id) REFERENCES teams(id);

ALTER TABLE projects
    ADD CONSTRAINT fk_projects_parent_id FOREIGN KEY (parent_id) REFERENCES projects(id);

ALTER TABLE projects
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE projects
    MODIFY updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE project_members
    RENAME COLUMN member_role TO role;

ALTER TABLE project_files
    ADD COLUMN plugin_id VARCHAR(36) NULL;

ALTER TABLE project_files
    ADD COLUMN created_by VARCHAR(36) NULL;

ALTER TABLE project_files
    ADD COLUMN updated_by VARCHAR(36) NULL;

ALTER TABLE project_files
    DROP COLUMN file_url;

ALTER TABLE project_files
    MODIFY size INT;

ALTER TABLE project_files
    MODIFY content TEXT;

ALTER TABLE project_files
    ADD CONSTRAINT fk_project_files_plugin_id FOREIGN KEY (plugin_id) REFERENCES plugins(id);

ALTER TABLE project_files
    ADD CONSTRAINT fk_project_files_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE project_files
    ADD CONSTRAINT fk_project_files_updated_by FOREIGN KEY (updated_by) REFERENCES users(id);

ALTER TABLE project_checklists
    ADD COLUMN description TEXT;

ALTER TABLE project_checklists
    ADD COLUMN assignee_user_id VARCHAR(36) NULL;

ALTER TABLE project_checklists
    ADD COLUMN tags JSON;

ALTER TABLE project_checklists
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE project_checklists
    ADD CONSTRAINT fk_project_checklists_assignee_user_id FOREIGN KEY (assignee_user_id) REFERENCES users(id);

ALTER TABLE chat_messages
    RENAME COLUMN metadata TO image_urls;

ALTER TABLE community_posts
    ADD COLUMN group_id VARCHAR(36) NULL;

ALTER TABLE community_posts
    ADD COLUMN images JSON;

ALTER TABLE community_posts
    ADD COLUMN channel VARCHAR(20);

ALTER TABLE community_posts
    ADD COLUMN visibility VARCHAR(20) DEFAULT 'public';

ALTER TABLE community_posts
    ADD COLUMN comments_count INT DEFAULT 0;

ALTER TABLE community_posts
    DROP COLUMN is_deleted;

ALTER TABLE community_posts
    MODIFY title VARCHAR(255) NULL;

ALTER TABLE community_posts
    MODIFY tags JSON;

ALTER TABLE community_posts
    ADD CONSTRAINT fk_community_posts_group_id FOREIGN KEY (group_id) REFERENCES community_groups(id);

ALTER TABLE comments
    RENAME TO community_comments;

ALTER TABLE community_comments
    RENAME COLUMN author_id TO user_id;

ALTER TABLE community_comments
    ADD COLUMN likes_count INT DEFAULT 0;

ALTER TABLE community_comments
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
