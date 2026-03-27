package com.ideaspark.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置类
 * 用于生成 Swagger UI 文档
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IdeaSpark API")
                        .version("0.0.1-SNAPSHOT")
                        .description("IdeaSpark 项目后端 API 文档")
                        .contact(new Contact()
                                .name("IdeaSpark Team")
                                .email("support@ideaspark.com")));
    }
}
