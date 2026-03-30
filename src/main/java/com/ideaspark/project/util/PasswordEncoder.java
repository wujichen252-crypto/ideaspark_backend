package com.ideaspark.project.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 * @description 使用 BCrypt 算法进行密码加密和验证
 * @author IdeaSpark
 */
@Component
public class PasswordEncoder {

    private final BCryptPasswordEncoder encoder;

    /**
     * 构造函数
     * 使用默认强度（10）的 BCrypt 加密
     */
    public PasswordEncoder() {
        this.encoder = new BCryptPasswordEncoder();
    }

    /**
     * 加密密码
     * @param plainPassword 明文密码
     * @return 加密后的密码哈希
     */
    public String encode(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return encoder.encode(plainPassword);
    }

    /**
     * 验证密码
     * @param plainPassword 明文密码
     * @param encodedPassword 加密后的密码哈希
     * @return 是否匹配
     */
    public boolean matches(String plainPassword, String encodedPassword) {
        if (plainPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(plainPassword, encodedPassword);
    }

    /**
     * 检查密码是否需要升级（重新加密）
     * @param encodedPassword 当前加密后的密码
     * @return 是否需要升级
     */
    public boolean needsUpgrade(String encodedPassword) {
        // BCrypt 会自动处理版本升级
        // 这里可以添加自定义逻辑，如检查加密强度
        return false;
    }
}
