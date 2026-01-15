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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        if (isBlank(request.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (isBlank(request.getEmail())) {
            throw new BusinessException("邮箱不能为空");
        }
        if (isBlank(request.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(sha256Hex(request.getPassword()));
        user.setRole("USER");

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
     * 删除用户
     */
    @Transactional
    public void deleteUsers(UserDeleteRequest request) {
        if (request == null || request.getUserIds() == null || request.getUserIds().isEmpty()) {
            throw new BusinessException("用户 ID 列表不能为空");
        }

        for (String id : request.getUserIds()) {
            if (id == null) {
                continue;
            }
            if (!userRepository.existsById(id)) {
                throw new BusinessException("用户不存在: " + id);
            }
            userRepository.deleteById(id);
        }
    }

    /**
     * Entity 转 Response DTO（手动映射）
     */
    private UserResponse toUserResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setRole(roleToCn(user.getRole()));
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
     * 判断字符串是否为空白
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 使用 SHA-256 对明文进行不可逆摘要
     */
    private String sha256Hex(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("密码处理失败");
        }
    }
}

