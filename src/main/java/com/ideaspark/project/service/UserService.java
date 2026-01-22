package com.ideaspark.project.service;

import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.request.UserCreateRequest;
import com.ideaspark.project.model.dto.request.UserDeleteRequest;
import com.ideaspark.project.model.dto.request.UserLoginRequest;
import com.ideaspark.project.model.dto.request.UserQueryRequest;
import com.ideaspark.project.model.dto.request.UserUpdateRequest;
import com.ideaspark.project.model.dto.response.LoginResponse;
import com.ideaspark.project.model.dto.response.UserResponse;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.UserRepository;
import com.ideaspark.project.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(UserLoginRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (isBlank(request.getEmail())) {
            throw new BusinessException("邮箱不能为空");
        }
        if (isBlank(request.getPassword())) {
            throw new BusinessException("密码不能为空");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("邮箱或密码错误"));

        if (!user.getPasswordHash().equals(sha256Hex(request.getPassword()))) {
            throw new BusinessException("邮箱或密码错误");
        }

        // Generate token using User ID as account/subject
        String token = jwtUtil.generateToken(String.valueOf(user.getId()), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(toUserResponse(user));
        return response;
    }

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

        for (Long id : request.getUserIds()) {
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
     * 更新用户信息
     */
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        if (userId == null) {
            throw new BusinessException("用户 ID 不能为空");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在: " + userId));

        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!user.getEmail().equals(request.getEmail())) {
                if (userRepository.existsByEmail(request.getEmail())) {
                    throw new BusinessException("邮箱已存在");
                }
                user.setEmail(request.getEmail());
            }
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPasswordHash(sha256Hex(request.getPassword()));
        }
        if (request.getPosition() != null) {
            user.setPosition(request.getPosition());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getPerWebsite() != null) {
            user.setPerWebsite(request.getPerWebsite());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getIsHide() != null) {
            user.setIsHide(request.getIsHide());
        }
        if (request.getIsNotifSys() != null) {
            user.setIsNotifSys(request.getIsNotifSys());
        }
        if (request.getIsNotifTrends() != null) {
            user.setIsNotifTrends(request.getIsNotifTrends());
        }
        if (request.getIsNotifPost() != null) {
            user.setIsNotifPost(request.getIsNotifPost());
        }

        User saved = userRepository.save(user);
        return toUserResponse(saved);
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
        dto.setIsHide(user.getIsHide());
        dto.setIsNotifSys(user.getIsNotifSys());
        dto.setIsNotifTrends(user.getIsNotifTrends());
        dto.setIsNotifPost(user.getIsNotifPost());
        dto.setBio(user.getBio());
        dto.setPosition(user.getPosition());
        dto.setAddress(user.getAddress());
        dto.setPerWebsite(user.getPerWebsite());
        dto.setPhone(user.getPhone());
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

