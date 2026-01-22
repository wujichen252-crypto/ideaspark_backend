package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.request.UserRegisterRequest;
import com.ideaspark.project.model.dto.request.UserDeleteRequest;
import com.ideaspark.project.model.dto.request.UserLoginRequest;
import com.ideaspark.project.model.dto.request.UserQueryRequest;
import com.ideaspark.project.model.dto.request.UserUpdateRequest;
import com.ideaspark.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
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
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
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
    public ResponseEntity<?> update(@RequestAttribute("userId") Long userId, @RequestBody UserUpdateRequest request) {
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
    public ResponseEntity<?> getAllUsers(@ModelAttribute UserQueryRequest request) {
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
    public ResponseEntity<?> deleteUsers(@RequestBody UserDeleteRequest request) {
        userService.deleteUsers(request);
        return ResponseEntity.ok(Map.of(
            "status", 200,
            "message", "删除成功"
        ));
    }
}

