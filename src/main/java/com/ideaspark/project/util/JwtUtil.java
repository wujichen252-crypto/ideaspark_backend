package com.ideaspark.project.util;

import com.ideaspark.project.config.JwtProperties;
import com.ideaspark.project.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    /**
     * 生成 JWT（HS256），默认包含 account/role 两个 claim
     */
    public String generateToken(String account, String role) {
        if (isBlank(account)) {
            throw new BusinessException("account 不能为空");
        }
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtProperties.getExpireSeconds());

        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setSubject(account)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(Map.of(
                        "account", account,
                        "role", role
                ))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 token 并返回 claims（解析失败会抛业务异常）
     */
    public Claims parseClaims(String token) {
        if (isBlank(token)) {
            throw new BusinessException("token 不能为空");
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new BusinessException("token 无效: " + e.getMessage());
        }
    }

    /**
     * 从 token 中读取 account
     */
    public String getAccount(String token) {
        Claims claims = parseClaims(token);
        Object account = claims.get("account");
        return account != null ? String.valueOf(account) : null;
    }

    /**
     * 校验 token 是否可被正确解析且未过期
     */
    public boolean validate(String token) {
        try {
            Claims claims = parseClaims(token);
            Date exp = claims.getExpiration();
            return exp != null && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取签名密钥（支持 Base64 或普通字符串 secret）
     */
    private SecretKey getSigningKey() {
        String secret = jwtProperties.getSecret();
        if (isBlank(secret)) {
            throw new BusinessException("jwt.secret 未配置");
        }

        byte[] keyBytes;
        if (looksLikeBase64(secret)) {
            keyBytes = Decoders.BASE64.decode(secret);
        } else {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 判断是否为 Base64 格式字符串
     */
    private boolean looksLikeBase64(String value) {
        String v = value.trim();
        return v.length() % 4 == 0 && v.matches("^[A-Za-z0-9+/=]+$");
    }

    /**
     * 判断字符串是否为空白
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

