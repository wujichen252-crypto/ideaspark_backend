package com.example.project.module.user.repository;

import com.example.project.module.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 * 负责用户实体的数据库操作
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return Optional<UserEntity>
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return Optional<UserEntity>
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return Optional<UserEntity>
     */
    Optional<UserEntity> findByPhone(String phone);

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return boolean
     */
    boolean existsByUsername(String username);

    /**
     * 判断邮箱是否存在
     *
     * @param email 邮箱
     * @return boolean
     */
    boolean existsByEmail(String email);

    /**
     * 判断手机号是否存在
     *
     * @param phone 手机号
     * @return boolean
     */
    boolean existsByPhone(String phone);

}