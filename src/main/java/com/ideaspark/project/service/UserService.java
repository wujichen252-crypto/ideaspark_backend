package com.ideaspark.project.service;

import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.request.UserCreateRequest;
import com.ideaspark.project.model.dto.request.UserDeleteRequest;
import com.ideaspark.project.model.dto.request.UserQueryRequest;
import com.ideaspark.project.model.dto.response.UserResponse;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 注册用户
     */
    @Transactional
    public UserResponse register(UserCreateRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (isBlank(request.getAccount())) {
            throw new BusinessException("账号不能为空");
        }
        if (isBlank(request.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (userRepository.existsByAccount(request.getAccount())) {
            throw new BusinessException("账号已存在");
        }

        User user = new User();
        user.setAccount(request.getAccount());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setRole("ADMIN");
        user.setState("N");
        user.setCreateTime(LocalDateTime.now());

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    /**
     * 分页查询用户
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> queryUsers(UserQueryRequest request) {
        int page = request != null && request.getPage() != null ? request.getPage() : 0;
        int size = request != null && request.getSize() != null ? request.getSize() : 10;

        if (page < 0) {
            throw new BusinessException("page 不能小于 0");
        }
        if (size <= 0 || size > 200) {
            throw new BusinessException("size 必须在 1~200 之间");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> result;
        if (request != null && !isBlank(request.getName())) {
            result = userRepository.findByUsernameContainingIgnoreCase(request.getName(), pageable);
        } else {
            result = userRepository.findAll(pageable);
        }

        return result.map(this::toUserResponse);
    }

    /**
     * 逻辑删除用户（将 state 置为 X）
     */
    @Transactional
    public void deleteUsers(UserDeleteRequest request) {
        if (request == null || request.getUserIds() == null || request.getUserIds().isEmpty()) {
            throw new BusinessException("用户 ID 列表不能为空");
        }

        for (Integer id : request.getUserIds()) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("用户不存在: " + id));
            user.setState("X");
            userRepository.save(user);
        }
    }

    /**
     * Entity 转 Response DTO（手动映射）
     */
    private UserResponse toUserResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setAccount(user.getAccount());
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setCreateTime(user.getCreateTime());
        dto.setRole(roleToCn(user.getRole()));
        dto.setState(stateToCn(user.getState()));
        return dto;
    }

    /**
     * 角色编码转中文
     */
    private String roleToCn(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "超级管理员";
        }
        if ("USER".equalsIgnoreCase(role)) {
            return "普通用户";
        }
        return role;
    }

    /**
     * 状态编码转中文
     */
    private String stateToCn(String state) {
        if ("N".equalsIgnoreCase(state)) {
            return "新建";
        }
        if ("A".equalsIgnoreCase(state)) {
            return "已激活";
        }
        if ("X".equalsIgnoreCase(state)) {
            return "已删除";
        }
        return state;
    }

    /**
     * 判断字符串是否为空白
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

