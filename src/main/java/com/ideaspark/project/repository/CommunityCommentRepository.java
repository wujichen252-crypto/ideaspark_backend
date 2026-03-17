package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, String> {

    /**
     * 查询帖子下的一级评论（parentId为null）
     */
    List<CommunityComment> findByPostIdAndParentIdIsNull(String postId);

    /**
     * 查询二级回复（根据parentId）
     */
    List<CommunityComment> findByParentId(String parentId);

    /**
     * 根据帖子ID查询所有评论
     */
    List<CommunityComment> findByPostId(String postId);
}
