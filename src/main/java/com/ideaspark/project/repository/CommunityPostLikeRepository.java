package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.CommunityPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, String> {

    /**
     * 检查用户是否已给帖子点赞
     */
    Optional<CommunityPostLike> findByPostIdAndUserId(String postId, Long userId);

    /**
     * 删除用户对帖子的点赞
     */
    @Modifying
    @Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM CommunityPostLike cpl WHERE cpl.post.id = :postId AND cpl.user.id = :userId")
    void deleteByPostIdAndUserId(@org.springframework.data.repository.query.Param("postId") String postId, @org.springframework.data.repository.query.Param("userId") Long userId);

    /**
     * 统计帖子的点赞总数
     */
    long countByPostId(String postId);

    /**
     * 检查用户是否点赞了指定帖子
     */
    boolean existsByPostIdAndUserId(String postId, Long userId);
}
