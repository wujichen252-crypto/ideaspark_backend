package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.request.UserRegisterRequest;
import com.ideaspark.project.model.dto.request.UserDeleteRequest;
import com.ideaspark.project.model.dto.request.UserLoginRequest;
import com.ideaspark.project.model.dto.request.UserQueryRequest;
import com.ideaspark.project.model.dto.request.UserUpdateRequest;
import com.ideaspark.project.service.UserService;
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
            @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(Map.of(
            "status", 200,
            "message", "登录成功",
            "data", userService.login(request)
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
}

