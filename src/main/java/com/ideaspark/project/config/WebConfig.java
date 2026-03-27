package com.ideaspark.project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    /**
     * 配置JWT拦截器
     * 排除不需要认证的公开接口
     * 注意：部分接口GET请求公开访问由拦截器内部处理
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/public/**",
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
                        "/webjars/**"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
