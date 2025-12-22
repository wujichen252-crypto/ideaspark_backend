package com.example.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

/**
 * 文件上传配置类
 * 配置文件上传的大小限制等参数
 */
@Configuration
public class FileUploadConfig {

    @Value("${custom.file.max-size:10MB}")
    private String maxFileSize;
    
    @Value("${custom.file.upload-path:/tmp/uploads/}")
    private String uploadPath;
    
    /**
     * 配置文件上传解析器
     *
     * @return MultipartConfigElement
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // 设置单个文件最大大小
        factory.setMaxFileSize(DataSize.parse(maxFileSize));
        
        // 设置总上传文件大小
        factory.setMaxRequestSize(DataSize.parse(maxFileSize));
        
        // 设置文件临时存储路径
        factory.setLocation(uploadPath);
        
        return factory.createMultipartConfig();
    }
    
}

