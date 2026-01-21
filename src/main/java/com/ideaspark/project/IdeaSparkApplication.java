package com.ideaspark.project;

import com.ideaspark.project.util.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class IdeaSparkApplication {

    /**
     * 应用入口
     */
    public static void main(String[] args) {
        // 启动前加载 .env，本地开发使用
        EnvLoader.loadDotEnv();
        SpringApplication.run(IdeaSparkApplication.class, args);
    }
}
