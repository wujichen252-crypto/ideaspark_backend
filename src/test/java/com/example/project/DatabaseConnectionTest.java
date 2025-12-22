package com.example.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 数据库连接测试类（专注版）
 * 使用 @DataJpaTest 仅加载 JPA 相关的配置，自动排除 MQ/Redis 等干扰
 * 使用 @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 强制使用 application.yml 中的真实数据库
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    /**
     * 测试真实数据库连接
     */
    @Test
    public void testConnection() throws SQLException {
        assertNotNull(dataSource, "数据源未注入成功");
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "无法获取数据库连接");
            assertTrue(connection.isValid(5), "数据库连接无效");
            System.out.println("========================================");
            System.out.println("✅ 数据库连接测试成功！");
            System.out.println("当前数据库: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("数据库版本: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("连接URL: " + connection.getMetaData().getURL());
            System.out.println("========================================");
        }
    }
}