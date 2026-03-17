package com.ideaspark.project.controller;

import com.ideaspark.project.model.entity.CommunityComment;
import com.ideaspark.project.model.entity.CommunityPost;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.CommunityCommentRepository;
import com.ideaspark.project.repository.CommunityPostRepository;
import com.ideaspark.project.repository.UserRepository;
import com.ideaspark.project.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 社区评论控制器
 * 接口路径：/api/community/comments
 */
@RestController
@RequestMapping("/api/community/comments")
public class CommunityCommentController {

    @Autowired
    private CommunityCommentRepository communityCommentRepository;

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 新增评论
     */
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Map<String, Object> commentData, 
                                           HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseUtil.error("请先登录", 401);
            }

            String postId = (String) commentData.get("postId");
            if (postId == null || postId.isEmpty()) {
                return ResponseUtil.error("帖子ID不能为空", 400);
            }

            String content = (String) commentData.get("content");
            if (content == null || content.trim().isEmpty()) {
                return ResponseUtil.error("评论内容不能为空", 400);
            }

            // 校验帖子存在性
            Optional<CommunityPost> postOpt = communityPostRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseUtil.error("关联的帖子不存在", 404);
            }

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseUtil.error("用户不存在", 404);
            }

            CommunityComment comment = new CommunityComment();
            comment.setPost(postOpt.get());
            comment.setUser(userOpt.get());
            comment.setContent(content);

            // 设置父评论（如果是回复）
            if (commentData.get("parentId") != null) {
                String parentId = (String) commentData.get("parentId");
                communityCommentRepository.findById(parentId).ifPresent(comment::setParent);
            }

            CommunityComment savedComment = communityCommentRepository.save(comment);

            // 更新帖子评论数
            CommunityPost post = postOpt.get();
            post.setCommentsCount(post.getCommentsCount() + 1);
            communityPostRepository.save(post);

            return ResponseUtil.created(savedComment);
        } catch (Exception e) {
            return ResponseUtil.error("发布评论失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 查询帖子下的一级评论
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getCommentsByPostId(@PathVariable String postId) {
        try {
            List<CommunityComment> comments = communityCommentRepository.findByPostIdAndParentIdIsNull(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseUtil.error("查询评论失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 查询评论的所有回复
     */
    @GetMapping("/replies/{parentId}")
    public ResponseEntity<?> getRepliesByParentId(@PathVariable String parentId) {
        try {
            List<CommunityComment> replies = communityCommentRepository.findByParentId(parentId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            return ResponseUtil.error("查询回复失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 查询帖子的所有评论（包括回复）
     */
    @GetMapping("/post/{postId}/all")
    public ResponseEntity<?> getAllCommentsByPostId(@PathVariable String postId) {
        try {
            List<CommunityComment> comments = communityCommentRepository.findByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseUtil.error("查询评论失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 修改评论
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable String commentId, 
                                           @RequestBody Map<String, Object> updateData,
                                           HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseUtil.error("请先登录", 401);
            }

            Optional<CommunityComment> commentOpt = communityCommentRepository.findById(commentId);
            if (commentOpt.isEmpty()) {
                return ResponseUtil.error("评论不存在", 404);
            }

            CommunityComment comment = commentOpt.get();
            // 检查是否是评论作者本人
            if (!comment.getUser().getId().equals(userId)) {
                return ResponseUtil.error("无权修改此评论", 403);
            }

            String content = (String) updateData.get("content");
            if (content != null && !content.trim().isEmpty()) {
                comment.setContent(content);
                return ResponseEntity.ok(communityCommentRepository.save(comment));
            } else {
                return ResponseUtil.error("修改的评论内容不能为空", 400);
            }
        } catch (Exception e) {
            return ResponseUtil.error("修改评论失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 删除评论（逻辑删除）
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseUtil.error("请先登录", 401);
            }

            Optional<CommunityComment> commentOpt = communityCommentRepository.findById(commentId);
            if (commentOpt.isEmpty()) {
                return ResponseUtil.error("评论不存在", 404);
            }

            CommunityComment comment = commentOpt.get();
            // 检查是否是评论作者本人
            if (!comment.getUser().getId().equals(userId)) {
                return ResponseUtil.error("无权删除此评论", 403);
            }

            // 物理删除评论
            communityCommentRepository.delete(comment);

            // 更新帖子评论数
            CommunityPost post = comment.getPost();
            if (post != null) {
                int newCount = Math.max(0, post.getCommentsCount() - 1);
                post.setCommentsCount(newCount);
                communityPostRepository.save(post);
            }

            return ResponseUtil.success("评论删除成功");
        } catch (Exception e) {
            return ResponseUtil.error("删除评论失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 更新评论点赞数
     */
    @PutMapping("/{commentId}/likes")
    public ResponseEntity<?> updateCommentLikes(@PathVariable String commentId, 
                                                @RequestParam Integer count) {
        try {
            if (count == null || count < 0) {
                return ResponseUtil.error("点赞数不能为负数", 400);
            }
            Optional<CommunityComment> commentOpt = communityCommentRepository.findById(commentId);
            if (commentOpt.isPresent()) {
                CommunityComment comment = commentOpt.get();
                comment.setLikesCount(count);
                return ResponseEntity.ok(communityCommentRepository.save(comment));
            }
            return ResponseUtil.error("评论不存在", 404);
        } catch (Exception e) {
            return ResponseUtil.error("更新点赞数失败：" + e.getMessage(), 500);
        }
    }
}
