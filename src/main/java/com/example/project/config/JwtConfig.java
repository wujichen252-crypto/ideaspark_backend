package com.example.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * JWT认证配置类
 * 配置JWT解码和认证转换
 */
@Configuration
public class JwtConfig {

    @Value("${custom.jwt.secret}")
    private String jwtSecret;

    /**
     * 配置JWT解码器
     * 
     * @return JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        // 使用对称密钥解码JWT
        // 注意：生产环境建议使用非对称密钥(RSA/ECDSA)
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                new javax.crypto.spec.SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")
        ).build();
        return jwtDecoder;
    }

    /**
     * 配置JWT认证转换器
     * 用于将JWT声明转换为Spring Security权限
     * 
     * @return JwtAuthenticationConverter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 设置权限声明名称
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        // 设置权限前缀
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
