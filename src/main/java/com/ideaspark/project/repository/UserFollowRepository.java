package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, String> {

    /**
     * 检查是否已关注
     */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId")
    Optional<UserFollow> findByFollowerIdAndFollowingId(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 查询用户的关注列表
     */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.follower.id = :followerId")
    List<UserFollow> findByFollowerId(@Param("followerId") Long followerId);

    /**
     * 查询用户的粉丝列表
     */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.following.id = :followingId")
    List<UserFollow> findByFollowingId(@Param("followingId") Long followingId);

    /**
     * 统计用户的关注数
     */
    @Query("SELECT COUNT(uf) FROM UserFollow uf WHERE uf.follower.id = :followerId")
    long countByFollowerId(@Param("followerId") Long followerId);

    /**
     * 统计用户的粉丝数
     */
    @Query("SELECT COUNT(uf) FROM UserFollow uf WHERE uf.following.id = :followingId")
    long countByFollowingId(@Param("followingId") Long followingId);

    /**
     * 取消关注（删除记录）
     */
    @Modifying
    @Query("DELETE FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId")
    void deleteByFollowerIdAndFollowingId(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 检查是否已关注
     */
    @Query("SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END FROM UserFollow uf WHERE uf.follower.id = :followerId AND uf.following.id = :followingId")
    boolean existsByFollowerIdAndFollowingId(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
}
