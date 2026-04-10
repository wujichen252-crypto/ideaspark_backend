package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.response.SecurityLogResponse;
import com.ideaspark.project.service.SecurityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 安全日志控制器
 */
@RestController
@RequestMapping("/api/security/logs")
@RequiredArgsConstructor
@Tag(name = "安全日志", description = "用户安全操作记录相关接口")
public class SecurityLogController {

    private final SecurityLogService securityLogService;

    /**
     * 获取用户的安全日志列表
     */
    @GetMapping
    @Operation(summary = "获取安全日志", description = "分页获取当前用户的安全操作记录")
    public ResponseEntity<?> getSecurityLogs(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "页码，默认1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量，默认20")
            @RequestParam(defaultValue = "20") int size) {
        Page<SecurityLogResponse> logs = securityLogService.getUserSecurityLogs(userId, page, size);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", Map.of(
                        "logs", logs.getContent(),
                        "total", logs.getTotalElements(),
                        "page", page,
                        "size", size,
                        "totalPages", logs.getTotalPages()
                )
        ));
    }
}
