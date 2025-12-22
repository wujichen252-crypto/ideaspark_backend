package com.example.project.module.user.controller;

import com.example.project.common.response.ApiResponse;
import com.example.project.module.user.model.dto.CreateUserDTO;
import com.example.project.module.user.model.vo.UserVO;
import com.example.project.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 创建用户
     *
     * @param dto 用户创建参数
     * @return ApiResponse<UserVO>
     */
    @PostMapping
    public ApiResponse<UserVO> createUser(@Valid @RequestBody CreateUserDTO dto) {
        return ApiResponse.success(userService.createUser(dto));
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return ApiResponse<UserVO>
     */
    @GetMapping("/{id}")
    public ApiResponse<UserVO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "用户不存在"));
    }

    /**
     * 查询所有用户
     *
     * @return ApiResponse<List<UserVO>>
     */
    @GetMapping
    public ApiResponse<List<UserVO>> getAllUsers() {
        return ApiResponse.success(userService.getAllUsers());
    }

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 用户状态
     * @return ApiResponse<Boolean>
     */
    @PutMapping("/{id}/status/{status}")
    public ApiResponse<Boolean> updateUserStatus(@PathVariable Long id, @PathVariable Integer status) {
        boolean success = userService.updateUserStatus(id, status);
        return success ? ApiResponse.success(true) : ApiResponse.error(404, "用户不存在");
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return ApiResponse<Boolean>
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        return success ? ApiResponse.success(true) : ApiResponse.error(404, "用户不存在");
    }

}