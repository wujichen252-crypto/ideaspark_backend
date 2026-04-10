package com.ideaspark.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek AI 配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {

    /**
     * API Key
     */
    private String apiKey;

    /**
     * API 基础 URL
     */
    private String baseUrl = "https://api.deepseek.com";

    /**
     * 默认模型
     */
    private String model = "deepseek-chat";

    /**
     * 默认温度参数 (0-2)
     */
    private Double temperature = 0.7;

    /**
     * 默认最大 token 数
     */
    private Integer maxTokens = 2000;

    /**
     * 连接超时（毫秒）
     */
    private Integer connectTimeout = 30000;

    /**
     * 读取超时（毫秒）
     */
    private Integer readTimeout = 60000;
}
