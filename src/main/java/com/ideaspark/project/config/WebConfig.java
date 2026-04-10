package com.ideaspark.project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 * @description 配置拦截器、CORS 等 Web 相关设置
 * @author IdeaSpark
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    /**
     * 配置拦截器
     * 1. 限流拦截器 - 防止 DDoS 攻击
     * 2. JWT 认证拦截器 - 处理用户认证
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 限流拦截器（最先执行）
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/actuator/**"
                );

        // JWT 认证拦截器
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/public/**",
                        "/api/admin/init/**",
                        "/api/market/projects/list",
                        "/api/market/projects/*",
                        "/api/community/groups",
                        "/api/community/groups/*",
                        "/api/community/groups/*/members",
                        "/api/community/likes/post/*/count",
                        "/api/community/likes/comment/*/count",
                        "/api/follows/user/*",
                        "/api/follows/user/*/following",
                        "/api/follows/user/*/followers",
                        "/api/follows/user/*/following/count",
                        "/api/follows/user/*/followers/count",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/actuator/**"
                );
    }

    /**
     * 注意：CORS 配置已移至 CorsConfig 类
     * 使用 CorsFilter 过滤器处理跨域，优先级更高，确保在 Spring Security 之前执行
     */
}
