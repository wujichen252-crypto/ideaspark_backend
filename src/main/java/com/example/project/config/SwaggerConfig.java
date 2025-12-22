package com.example.project.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger3配置类
 * 配置API文档生成和展示
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置OpenAPI信息
     * 包括API基本信息、许可证、外部文档等
     *
     * @return OpenAPI
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Spring Boot Scaffold API")
                        .description("Spring Boot 项目脚手架 API 文档")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Spring Boot 项目脚手架文档")
                        .url("https://example.com/docs"));
    }

}
