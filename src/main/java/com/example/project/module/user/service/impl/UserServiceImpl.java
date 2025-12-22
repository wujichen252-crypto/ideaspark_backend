package com.example.project.module.user.service.impl;

import com.example.project.common.exception.BusinessException;
import com.example.project.module.user.model.dto.CreateUserDTO;
import com.example.project.module.user.model.entity.UserEntity;
import com.example.project.module.user.model.vo.UserVO;
import com.example.project.module.user.repository.UserRepository;
import com.example.project.module.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 创建用户
     *
     * @param dto 用户创建参数
     * @return UserVO
     */
    @Override
    @Transactional
    public UserVO createUser(CreateUserDTO dto) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(400, "邮箱已存在");
        }

        // 检查手机号是否已存在
        if (dto.getPhone() != null && userRepository.existsByPhone(dto.getPhone())) {
            throw new BusinessException(400, "手机号已存在");
        }

        // 创建用户实体
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(dto, userEntity);
        userEntity.setStatus(1); // 默认启用状态

        // 保存用户
        userEntity = userRepository.save(userEntity);

        // 转换为VO返回
        return convertToVO(userEntity);
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return UserVO
     */
    @Override
    public Optional<UserVO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToVO);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return UserVO
     */
    @Override
    public Optional<UserVO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToVO);
    }

    /**
     * 查询所有用户
     *
     * @return List<UserVO>
     */
    @Override
    public List<UserVO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 用户状态
     * @return boolean
     */
    @Override
    @Transactional
    public boolean updateUserStatus(Long id, Integer status) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(status);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return boolean
     */
    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 将用户实体转换为VO
     *
     * @param userEntity 用户实体
     * @return UserVO
     */
    private UserVO convertToVO(UserEntity userEntity) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userEntity, userVO);
        return userVO;
    }

}