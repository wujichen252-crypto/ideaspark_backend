package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.request.UserCreateRequest;
import com.ideaspark.project.model.dto.request.UserDeleteRequest;
import com.ideaspark.project.model.dto.request.UserQueryRequest;
import com.ideaspark.project.service.UserService;
import com.ideaspark.project.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserCreateRequest request) {
        return ResponseUtil.ok("注册成功", userService.register(request));
    }

    /**
     * 查询用户（分页）
     */
    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers(@ModelAttribute UserQueryRequest request) {
        Page<?> page = userService.queryUsers(request);
        return ResponseUtil.ok("查询成功", page);
    }

    /**
     * 删除用户（逻辑删除）
     */
    @PostMapping("/deleteUsers")
    public ResponseEntity<?> deleteUsers(@RequestBody UserDeleteRequest request) {
        userService.deleteUsers(request);
        return ResponseUtil.ok("删除成功");
    }
}

