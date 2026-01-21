package com.ideaspark.project.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    /**
     * 仅执行迁移，不执行清空数据库
     * 用于避免生产/测试环境在启动时发生清库风险
     */
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            flyway.migrate();
        };
    }
}
