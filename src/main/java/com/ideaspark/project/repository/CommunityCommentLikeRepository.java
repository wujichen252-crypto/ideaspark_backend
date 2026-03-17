package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.CommunityCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityCommentLikeRepository extends JpaRepository<CommunityCommentLike, String> {

    /**
     * 检查用户是否已给评论点赞
     */
    Optional<CommunityCommentLike> findByCommentIdAndUserId(String commentId, Long userId);

    /**
     * 删除用户对评论的点赞
     */
    void deleteByCommentIdAndUserId(String commentId, Long userId);

    /**
     * 统计评论的点赞总数
     */
    long countByCommentId(String commentId);

    /**
     * 检查用户是否点赞了指定评论
     */
    boolean existsByCommentIdAndUserId(String commentId, Long userId);
}
