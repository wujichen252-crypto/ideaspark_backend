package com.ideaspark.project.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流配置类
 * @description 基于 Bucket4j 实现请求限流，防止 DDoS 攻击
 * @author IdeaSpark
 */
@Slf4j
@Configuration
public class RateLimitConfig {

    @Value("${app.rate-limit.enabled:true}")
    private boolean enabled;

    @Value("${app.rate-limit.capacity:100}")
    private long capacity;

    @Value("${app.rate-limit.refill-tokens:10}")
    private long refillTokens;

    @Value("${app.rate-limit.refill-period:1}")
    private long refillPeriod;

    /**
     * 存储每个 IP 的限流桶
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * 获取或创建限流桶
     * @param key 限流键（通常是 IP 地址或用户 ID）
     * @return Bucket 限流桶
     */
    public Bucket getBucket(String key) {
        if (!enabled) {
            // 如果限流未启用，返回一个无限容量的桶（使用非常大的容量模拟）
            return Bucket.builder()
                    .addLimit(Bandwidth.simple(Long.MAX_VALUE, Duration.ofNanos(1)))
                    .build();
        }

        return buckets.computeIfAbsent(key, this::createNewBucket);
    }

    /**
     * 创建新的限流桶
     * @param key 限流键
     * @return Bucket 新的限流桶
     */
    private Bucket createNewBucket(String key) {
        log.debug("创建新的限流桶 for key: {}", key);

        // 配置限流策略：令牌桶算法
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(refillTokens, Duration.ofMinutes(refillPeriod)));

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * 移除限流桶
     * @param key 限流键
     */
    public void removeBucket(String key) {
        buckets.remove(key);
    }

    /**
     * 清空所有限流桶
     */
    public void clearAllBuckets() {
        buckets.clear();
    }

    /**
     * 检查限流是否启用
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }
}
