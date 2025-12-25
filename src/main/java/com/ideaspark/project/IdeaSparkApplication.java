package com.ideaspark.project;

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
        SpringApplication.run(IdeaSparkApplication.class, args);
    }
}
