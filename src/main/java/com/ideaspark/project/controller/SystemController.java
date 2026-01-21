package com.ideaspark.project.controller;

import com.ideaspark.project.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class SystemController {

    /**
     * 根路径（主页）：避免直接访问 / 出现 404
     */
    @GetMapping
    public ResponseEntity<?> home() {
        return ResponseUtil.ok("IdeaSpark 后端服务已启动", Map.of(
                "version", "0.0.1-SNAPSHOT",
                "docs", "/actuator/health, /api/user/*"
        ));
    }

    /**
     * 简单健康检查：/ping
     */
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseUtil.ok("pong");
    }

    /**
     * API 根路径说明：/api
     */
    @GetMapping("/api")
    public ResponseEntity<?> apiRoot() {
        return ResponseUtil.ok("API 根路径", Map.of(
                "user", "/api/user/login, /api/user/register, /api/user/getAllUsers, /api/user/deleteUsers"
        ));
    }
}
