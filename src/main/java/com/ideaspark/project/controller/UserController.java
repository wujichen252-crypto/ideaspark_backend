package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.request.UserRegisterRequest;
import com.ideaspark.project.model.dto.request.UserDeleteRequest;
import com.ideaspark.project.model.dto.request.UserLoginRequest;
import com.ideaspark.project.model.dto.request.UserQueryRequest;
import com.ideaspark.project.model.dto.request.UserUpdateRequest;
import com.ideaspark.project.service.UserService;
import com.ideaspark.project.service.SecurityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器
 * 提供用户注册、登录、信息管理等接口
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、登录、信息管理等接口")
public class UserController {

    private final UserService userService;
    private final SecurityLogService securityLogService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过邮箱和密码登录系统")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "邮箱或密码错误")
    })
    public ResponseEntity<?> login(
            @Parameter(description = "登录请求参数", required = true)
            @RequestBody UserLoginRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String device = parseDevice(userAgent);
        return ResponseEntity.ok(Map.of(
            "status", 200,
            "message", "登录成功",
            "data", userService.login(request, ipAddress, device, userAgent)
        ));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册账号")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "参数错误或邮箱已存在")
    })
    public ResponseEntity<?> register(
            @Parameter(description = "注册请求参数", required = true)
            @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(Map.of(
            "status", 200,
            "message", "注册成功",
            "data", userService.register(request)
        ));
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的基本信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "用户信息更新参数", required = true)
            @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(Map.of(
            "status", 200,
            "message", "更新成功",
            "data", userService.updateUser(userId, request)
        ));
    }

    /**
     * 查询用户（分页）
     */
    @GetMapping("/getAllUsers")
    @Operation(summary = "查询用户列表", description = "分页查询用户列表，支持按用户名搜索")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<?> getAllUsers(
            @Parameter(description = "用户查询参数")
            @ModelAttribute UserQueryRequest request) {
        Page<?> page = userService.queryUsers(request);
        return ResponseEntity.ok(Map.of(
            "status", 200,
            "message", "查询成功",
            "data", page
        ));
    }

    /**
     * 删除用户
     */
    @PostMapping("/deleteUsers")
    @Operation(summary = "删除用户", description = "批量删除用户（管理员权限）")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ResponseEntity<?> deleteUsers(
            @Parameter(description = "用户删除请求参数", required = true)
            @RequestBody UserDeleteRequest request) {
        userService.deleteUsers(request);
        return ResponseEntity.ok(Map.of(
            "status", 200,
            "message", "删除成功"
        ));
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "修改成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录或旧密码错误")
    })
    public ResponseEntity<?> changePassword(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "密码修改参数", required = true)
            @RequestBody java.util.Map<String, String> request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "message", "旧密码和新密码不能为空"
            ));
        }
        
        // 更新用户信息（包含密码修改）
        com.ideaspark.project.model.dto.request.UserUpdateRequest updateRequest = 
            new com.ideaspark.project.model.dto.request.UserUpdateRequest();
        updateRequest.setPassword(newPassword);
        userService.updateUser(userId, updateRequest);
        
        // 记录密码修改日志
        String ipAddress = getClientIpAddress(httpRequest);
        securityLogService.recordPasswordChange(userId, ipAddress);
        
        return ResponseEntity.ok(Map.of(
            "status", 200,
            "message", "密码修改成功"
        ));
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取用户统计", description = "获取当前用户的统计数据（帖子数、项目数等）")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<?> getUserStats(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId) {
        var stats = userService.getUserStats(userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", stats
        ));
    }

    // ============ 私有辅助方法 ============

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(jakarta.servlet.http.HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    /**
     * 解析设备信息
     */
    private String parseDevice(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        String ua = userAgent.toLowerCase();
        String os = "Unknown";
        String browser = "Unknown";

        // 操作系统
        if (ua.contains("windows")) {
            os = "Windows";
        } else if (ua.contains("macintosh") || ua.contains("mac os")) {
            os = "macOS";
        } else if (ua.contains("linux")) {
            os = "Linux";
        } else if (ua.contains("android")) {
            os = "Android";
        } else if (ua.contains("iphone") || ua.contains("ipad")) {
            os = "iOS";
        }

        // 浏览器
        if (ua.contains("chrome") && !ua.contains("edg")) {
            browser = "Chrome";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            browser = "Safari";
        } else if (ua.contains("firefox")) {
            browser = "Firefox";
        } else if (ua.contains("edg")) {
            browser = "Edge";
        }

        return browser + " / " + os;
    }
}

