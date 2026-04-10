package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    boolean existsByEmail(String email);

    /**
     * 随机获取指定数量的用户
     */
    @Query(value = "SELECT * FROM users ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<User> findRandomUsers(@Param("limit") int limit);

    /**
     * 获取推荐用户（排除已关注用户）
     * 优先返回有头像和简介的活跃用户
     */
    @Query(value = "SELECT * FROM users WHERE id NOT IN (:excludeIds) ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<User> findRecommendUsers(@Param("excludeIds") List<Long> excludeIds, @Param("limit") int limit);
}

