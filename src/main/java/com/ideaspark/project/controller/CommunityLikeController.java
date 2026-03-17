package com.ideaspark.project.controller;

import com.ideaspark.project.model.entity.CommunityComment;
import com.ideaspark.project.model.entity.CommunityCommentLike;
import com.ideaspark.project.model.entity.CommunityPost;
import com.ideaspark.project.model.entity.CommunityPostLike;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.CommunityCommentLikeRepository;
import com.ideaspark.project.repository.CommunityCommentRepository;
import com.ideaspark.project.repository.CommunityPostLikeRepository;
import com.ideaspark.project.repository.CommunityPostRepository;
import com.ideaspark.project.repository.UserRepository;
import com.ideaspark.project.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 社区点赞控制器
 * 接口路径：/api/community/likes
 */
@RestController
@RequestMapping("/api/community/likes")
public class CommunityLikeController {

    @Autowired
    private CommunityPostLikeRepository postLikeRepository;

    @Autowired
    private CommunityCommentLikeRepository commentLikeRepository;

    @Autowired
    private CommunityPostRepository postRepository;

    @Autowired
    private CommunityCommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== 帖子点赞 ====================

    /**
     * 给帖子点赞
     */
    @PostMapping("/post/{postId}")
    public ResponseEntity<?> likePost(@PathVariable String postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<CommunityPost> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ResponseUtil.error("帖子不存在", 404);
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseUtil.error("用户不存在", 404);
        }

        Optional<CommunityPostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            return ResponseUtil.error("已给该帖子点赞", 400);
        }

        // 新增点赞
        CommunityPostLike postLike = new CommunityPostLike();
        postLike.setPost(postOpt.get());
        postLike.setUser(userOpt.get());
        postLikeRepository.save(postLike);

        // 更新点赞数
        long likeCount = postLikeRepository.countByPostId(postId);
        CommunityPost post = postOpt.get();
        post.setLikesCount((int) likeCount);
        postRepository.save(post);

        return ResponseUtil.success("帖子点赞成功");
    }

    /**
     * 取消帖子点赞
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<?> cancelPostLike(@PathVariable String postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<CommunityPostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isEmpty()) {
            return ResponseUtil.error("未给该帖子点赞", 400);
        }

        // 删除点赞
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);

        // 更新点赞数
        long likeCount = postLikeRepository.countByPostId(postId);
        Optional<CommunityPost> postOpt = postRepository.findById(postId);
        postOpt.ifPresent(p -> {
            p.setLikesCount((int) likeCount);
            postRepository.save(p);
        });

        return ResponseUtil.success("取消帖子点赞成功");
    }

    /**
     * 查询帖子点赞数
     */
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<?> getPostLikeCount(@PathVariable String postId) {
        long count = postLikeRepository.countByPostId(postId);
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    /**
     * 检查用户是否给帖子点赞
     */
    @GetMapping("/post/{postId}/check")
    public ResponseEntity<?> checkPostLike(@PathVariable String postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(java.util.Map.of("liked", false));
        }
        boolean liked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        return ResponseEntity.ok(java.util.Map.of("liked", liked));
    }

    // ==================== 评论点赞 ====================

    /**
     * 给评论点赞
     */
    @PostMapping("/comment/{commentId}")
    public ResponseEntity<?> likeComment(@PathVariable String commentId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<CommunityComment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            return ResponseUtil.error("评论不存在", 404);
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseUtil.error("用户不存在", 404);
        }

        Optional<CommunityCommentLike> existingLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if (existingLike.isPresent()) {
            return ResponseUtil.error("已给该评论点赞", 400);
        }

        // 新增点赞
        CommunityCommentLike commentLike = new CommunityCommentLike();
        commentLike.setComment(commentOpt.get());
        commentLike.setUser(userOpt.get());
        commentLikeRepository.save(commentLike);

        // 更新点赞数
        long likeCount = commentLikeRepository.countByCommentId(commentId);
        CommunityComment comment = commentOpt.get();
        comment.setLikesCount((int) likeCount);
        commentRepository.save(comment);

        return ResponseUtil.success("评论点赞成功");
    }

    /**
     * 取消评论点赞
     */
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> cancelCommentLike(@PathVariable String commentId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<CommunityCommentLike> existingLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if (existingLike.isEmpty()) {
            return ResponseUtil.error("未给该评论点赞", 400);
        }

        // 删除点赞
        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);

        // 更新点赞数
        long likeCount = commentLikeRepository.countByCommentId(commentId);
        Optional<CommunityComment> commentOpt = commentRepository.findById(commentId);
        commentOpt.ifPresent(c -> {
            c.setLikesCount((int) likeCount);
            commentRepository.save(c);
        });

        return ResponseUtil.success("取消评论点赞成功");
    }

    /**
     * 查询评论点赞数
     */
    @GetMapping("/comment/{commentId}/count")
    public ResponseEntity<?> getCommentLikeCount(@PathVariable String commentId) {
        long count = commentLikeRepository.countByCommentId(commentId);
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    /**
     * 检查用户是否给评论点赞
     */
    @GetMapping("/comment/{commentId}/check")
    public ResponseEntity<?> checkCommentLike(@PathVariable String commentId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(java.util.Map.of("liked", false));
        }
        boolean liked = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        return ResponseEntity.ok(java.util.Map.of("liked", liked));
    }
}
