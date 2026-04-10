package com.ideaspark.project.service;

import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.request.UserDeleteRequest;
import com.ideaspark.project.model.dto.request.UserLoginRequest;
import com.ideaspark.project.model.dto.request.UserQueryRequest;
import com.ideaspark.project.model.dto.request.UserRegisterRequest;
import com.ideaspark.project.model.dto.request.UserUpdateRequest;
import com.ideaspark.project.model.dto.response.LoginResponse;
import com.ideaspark.project.model.dto.response.UserResponse;
import com.ideaspark.project.model.entity.Team;
import com.ideaspark.project.model.entity.TeamMember;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.TeamMemberRepository;
import com.ideaspark.project.repository.TeamRepository;
import com.ideaspark.project.repository.UserRepository;
import com.ideaspark.project.util.JwtUtil;
import com.ideaspark.project.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户管理业务逻辑层
 * @description 提供用户注册、登录、信息管理等核心业务逻辑
 * @author IdeaSpark
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final SecurityLogService securityLogService;

    /**
     * 用户登录
     * @param request 登录请求参数
     * @param ipAddress IP地址
     * @param device 设备信息
     * @param userAgent UserAgent
     * @return 登录响应，包含 Token 和用户信息
     * @throws BusinessException 当邮箱或密码错误时抛出
     */
    @Transactional(readOnly = true)
    public LoginResponse login(UserLoginRequest request, String ipAddress, String device, String userAgent) {
        validateLoginRequest(request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("邮箱或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("邮箱或密码错误");
        }

        String token = jwtUtil.generateToken(String.valueOf(user.getId()), user.getRole());

        // 记录登录成功日志
        securityLogService.recordLoginSuccess(user.getId(), ipAddress, device, userAgent);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(toUserResponse(user));
        return response;
    }

    /**
     * 注册用户
     * @param request 注册请求参数
     * @return 用户响应信息
     * @throws BusinessException 当参数错误或邮箱已存在时抛出
     */
    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        User user = createUserFromRequest(request);
        User saved = userRepository.save(user);

        createPersonalTeam(saved);

        return toUserResponse(saved);
    }

    /**
     * 分页查询用户
     * @param request 查询请求参数
     * @return 用户分页列表
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> queryUsers(UserQueryRequest request) {
        int page = request != null && request.getPage() != null ? request.getPage() : 0;
        int size = request != null && request.getSize() != null ? request.getSize() : 10;

        validatePageParams(page, size);

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
     * @param request 删除请求参数
     * @throws BusinessException 当用户不存在时抛出
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
     * @param userId 用户 ID
     * @param request 更新请求参数
     * @return 更新后的用户信息
     * @throws BusinessException 当用户不存在或邮箱已存在时抛出
     */
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        if (userId == null) {
            throw new BusinessException("用户 ID 不能为空");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在: " + userId));

        updateUserFields(user, request);

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    // ============ 私有方法 ============

    /**
     * 验证登录请求参数
     */
    private void validateLoginRequest(UserLoginRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (isBlank(request.getEmail())) {
            throw new BusinessException("邮箱不能为空");
        }
        if (isBlank(request.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
    }

    /**
     * 验证注册请求参数
     */
    private void validateRegisterRequest(UserRegisterRequest request) {
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
        if (request.getPassword().length() < 6) {
            throw new BusinessException("密码长度不能少于 6 位");
        }
    }

    /**
     * 验证分页参数
     */
    private void validatePageParams(int page, int size) {
        if (page < 0) {
            throw new BusinessException("page 不能小于 0");
        }
        if (size <= 0 || size > 200) {
            throw new BusinessException("size 必须在 1~200 之间");
        }
    }

    /**
     * 根据注册请求创建用户实体
     */
    private User createUserFromRequest(UserRegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        return user;
    }

    /**
     * 创建个人团队
     */
    private void createPersonalTeam(User user) {
        Team team = new Team();
        team.setOwner(user);
        team.setName(user.getUsername());
        team.setIsPersonal(true);
        team.setTeamSize(1);
        Team savedTeam = teamRepository.save(team);

        TeamMember ownerMember = new TeamMember();
        ownerMember.setTeam(savedTeam);
        ownerMember.setUser(user);
        ownerMember.setRole("owner");
        teamMemberRepository.save(ownerMember);
    }

    /**
     * 更新用户字段
     */
    private void updateUserFields(User user, UserUpdateRequest request) {
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
            if (request.getPassword().length() < 6) {
                throw new BusinessException("密码长度不能少于 6 位");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
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
    }

    /**
     * Entity 转 Response DTO（手动映射）
     * TODO: 后续使用 MapStruct 自动生成
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
     * 获取用户统计数据
     */
    @Transactional(readOnly = true)
    public com.ideaspark.project.model.dto.response.UserStatsResponse getUserStats(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 获取用户帖子数量
        long postCount = user.getCommunityPosts() != null ? user.getCommunityPosts().size() : 0;
        
        // 获取用户项目数量
        long projectCount = user.getOwnedProjects() != null ? user.getOwnedProjects().size() : 0;
        
        // 获取关注数
        long followingCount = user.getFollowingCount() != null ? user.getFollowingCount() : 0;
        
        // 获取粉丝数
        long followerCount = user.getFollowersCount() != null ? user.getFollowersCount() : 0;
        
        return com.ideaspark.project.model.dto.response.UserStatsResponse.builder()
                .postCount(postCount)
                .projectCount(projectCount)
                .followingCount(followingCount)
                .followerCount(followerCount)
                .build();
    }
}
