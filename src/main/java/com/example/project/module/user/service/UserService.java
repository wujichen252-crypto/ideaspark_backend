package com.example.project.module.user.service;

import com.example.project.module.user.model.dto.CreateUserDTO;
import com.example.project.module.user.model.vo.UserVO;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 * 定义用户相关的业务方法
 */
public interface UserService {

    /**
     * 创建用户
     *
     * @param dto 用户创建参数
     * @return UserVO
     */
    UserVO createUser(CreateUserDTO dto);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return UserVO
     */
    Optional<UserVO> getUserById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return UserVO
     */
    Optional<UserVO> getUserByUsername(String username);

    /**
     * 查询所有用户
     *
     * @return List<UserVO>
     */
    List<UserVO> getAllUsers();

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 用户状态
     * @return boolean
     */
    boolean updateUserStatus(Long id, Integer status);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return boolean
     */
    boolean deleteUser(Long id);

}