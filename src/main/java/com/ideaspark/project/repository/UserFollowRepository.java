package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, String> {

    /**
     * 检查是否已关注
     */
    Optional<UserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * 查询用户的关注列表
     */
    List<UserFollow> findByFollowerId(Long followerId);

    /**
     * 查询用户的粉丝列表
     */
    List<UserFollow> findByFollowingId(Long followingId);

    /**
     * 统计用户的关注数
     */
    long countByFollowerId(Long followerId);

    /**
     * 统计用户的粉丝数
     */
    long countByFollowingId(Long followingId);

    /**
     * 取消关注（删除记录）
     */
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * 检查是否已关注
     */
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
