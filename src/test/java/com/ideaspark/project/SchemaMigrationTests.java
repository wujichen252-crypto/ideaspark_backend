package com.ideaspark.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SchemaMigrationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 校验 Flyway 迁移已创建文档定义的全部表
     */
    @Test
    void shouldCreateAllTablesByFlywayMigrations() {
        assertNotNull(jdbcTemplate);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_follows", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teams", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM team_members", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM team_resources", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM projects", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM project_members", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM project_modules", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM project_files", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM project_checklists", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plugins", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM project_plugins", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chat_sessions", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chat_messages", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM community_groups", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM community_group_members", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM community_posts", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM community_comments", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM community_post_likes", Integer.class);
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM community_comment_likes", Integer.class);
    }
}
