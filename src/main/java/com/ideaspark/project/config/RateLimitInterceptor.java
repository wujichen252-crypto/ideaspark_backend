package com.ideaspark.project.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 限流拦截器
 * @description 拦截请求并进行限流检查
 * @author IdeaSpark
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitConfig rateLimitConfig;

    /**
     * 限流响应头名称
     */
    private static final String HEADER_RETRY_AFTER = "X-RateLimit-Retry-After";
    private static final String HEADER_LIMIT = "X-RateLimit-Limit";
    private static final String HEADER_REMAINING = "X-RateLimit-Remaining";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果限流未启用，直接放行
        if (!rateLimitConfig.isEnabled()) {
            return true;
        }

        // 获取客户端 IP 作为限流键
        String clientIp = getClientIp(request);
        Bucket bucket = rateLimitConfig.getBucket(clientIp);

        // 尝试消费一个令牌
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // 请求通过，设置限流响应头
            response.setHeader(HEADER_LIMIT, String.valueOf(bucket.getAvailableTokens() + 1));
            response.setHeader(HEADER_REMAINING, String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            // 请求被限流
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.setStatus(429); // Too Many Requests
            response.setHeader(HEADER_RETRY_AFTER, String.valueOf(waitForRefill));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(String.format(
                "{\"status\":429,\"message\":\"请求过于频繁，请 %d 秒后再试\",\"data\":null}",
                waitForRefill
            ));
            log.warn("IP [{}] 请求过于频繁，被限流", clientIp);
            return false;
        }
    }

    /**
     * 获取客户端真实 IP 地址
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果有多个 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
