package com.ideaspark.project.config;

import com.ideaspark.project.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * JWT认证拦截器
 * 对需要认证的接口进行token验证
 * 支持部分接口GET请求公开访问，POST/PUT/DELETE需要认证
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 允许GET请求公开访问的路径（但如果有token则解析）
     */
    private static final List<String> PUBLIC_GET_PATHS = Arrays.asList(
            "/api/community/posts",
            "/api/community/posts/"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        boolean isPublicGetPath = PUBLIC_GET_PATHS.stream()
                .anyMatch(path -> requestURI.equals(path) || requestURI.startsWith(path.replace("/*", "")));

        if ("GET".equalsIgnoreCase(method) && isPublicGetPath) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    if (jwtUtil.validate(token)) {
                        String userId = jwtUtil.getAccount(token);
                        if (userId != null) {
                            request.setAttribute("userId", Long.valueOf(userId));
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\":401,\"message\":\"请先登录\",\"data\":null}");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            if (!jwtUtil.validate(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\":401,\"message\":\"token无效\",\"data\":null}");
                return false;
            }

            String userId = jwtUtil.getAccount(token);
            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\":401,\"message\":\"token无效\",\"data\":null}");
                return false;
            }

            request.setAttribute("userId", Long.valueOf(userId));
            return true;

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\":401,\"message\":\"认证失败\",\"data\":null}");
            return false;
        }
    }
}
