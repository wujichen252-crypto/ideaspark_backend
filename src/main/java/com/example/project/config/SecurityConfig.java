package com.example.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 * 配置认证、授权和安全规则
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置密码加密方式
     * 使用BCrypt算法进行密码加密
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全过滤链
     * 定义URL的访问规则和认证方式
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF保护，前后端分离项目中通常关闭
                .csrf(csrf -> csrf.disable())
                // 配置请求授权规则
                .authorizeHttpRequests(authorize -> authorize
                        // 允许所有用户访问Swagger相关资源
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        // 允许所有用户访问Actuator健康检查端点
                        .requestMatchers("/actuator/health").permitAll()
                        // 允许所有用户访问静态资源
                        .requestMatchers("/static/**").permitAll()
                        // 允许所有用户访问根路径
                        .requestMatchers("/").permitAll()
                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )
                // 配置基本认证
                .httpBasic(httpBasic -> {})
                // 配置表单登录
                .formLogin(formLogin -> formLogin
                        .defaultSuccessUrl("/", true)
                );

        return http.build();
    }

}
