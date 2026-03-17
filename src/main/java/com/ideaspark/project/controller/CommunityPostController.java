package com.ideaspark.project.controller;

import com.ideaspark.project.model.entity.CommunityPost;
import com.ideaspark.project.model.entity.Project;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.CommunityPostRepository;
import com.ideaspark.project.repository.ProjectRepository;
import com.ideaspark.project.repository.UserRepository;
import com.ideaspark.project.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 社区帖子控制器
 * 接口路径：/api/community/posts
 */
@RestController
@RequestMapping("/api/community/posts")
public class CommunityPostController {

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * 创建帖子
     */
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> postData, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseUtil.error("请先登录", 401);
            }

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseUtil.error("用户不存在", 404);
            }

            CommunityPost post = new CommunityPost();
            post.setAuthor(userOpt.get());
            post.setTitle((String) postData.get("title"));
            post.setContent((String) postData.get("content"));
            post.setImages((String) postData.get("images"));
            post.setTags((String) postData.get("tags"));
            post.setChannel((String) postData.get("channel"));
            post.setVisibility((String) postData.getOrDefault("visibility", "PUBLIC"));

            // 关联项目（如果提供了projectId）
            if (postData.get("projectId") != null) {
                String projectId = (String) postData.get("projectId");
                projectRepository.findById(projectId).ifPresent(post::setProject);
            }

            CommunityPost savedPost = communityPostRepository.save(post);
            return ResponseUtil.created(savedPost);
        } catch (Exception e) {
            return ResponseUtil.error("创建帖子失败：" + e.getMessage(), 400);
        }
    }

    /**
     * 查询所有帖子
     */
    @GetMapping
    public ResponseEntity<List<CommunityPost>> getAllPosts() {
        List<CommunityPost> posts = communityPostRepository.findAll();
        return ResponseEntity.ok(posts);
    }

    /**
     * 根据ID查询帖子详情
     */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable String postId) {
        Optional<CommunityPost> postOpt = communityPostRepository.findById(postId);
        if (postOpt.isPresent()) {
            CommunityPost post = postOpt.get();
            // 浏览数+1
            post.setViewsCount(post.getViewsCount() + 1);
            communityPostRepository.save(post);
            return ResponseEntity.ok(post);
        } else {
            return ResponseUtil.error("帖子不存在", 404);
        }
    }

    /**
     * 修改帖子
     */
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable String postId, 
                                        @RequestBody Map<String, Object> updateData,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<CommunityPost> postOpt = communityPostRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ResponseUtil.error("帖子不存在", 404);
        }

        CommunityPost post = postOpt.get();
        // 检查是否是作者本人
        if (!post.getAuthor().getId().equals(userId)) {
            return ResponseUtil.error("无权修改此帖子", 403);
        }

        // 更新字段
        if (updateData.get("title") != null) {
            post.setTitle((String) updateData.get("title"));
        }
        if (updateData.get("content") != null) {
            post.setContent((String) updateData.get("content"));
        }
        if (updateData.get("images") != null) {
            post.setImages((String) updateData.get("images"));
        }
        if (updateData.get("tags") != null) {
            post.setTags((String) updateData.get("tags"));
        }
        if (updateData.get("visibility") != null) {
            post.setVisibility((String) updateData.get("visibility"));
        }

        CommunityPost updatedPost = communityPostRepository.save(post);
        return ResponseEntity.ok(updatedPost);
    }

    /**
     * 帖子逻辑删除（修改状态）
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<CommunityPost> postOpt = communityPostRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ResponseUtil.error("帖子不存在", 404);
        }

        CommunityPost post = postOpt.get();
        // 检查是否是作者本人
        if (!post.getAuthor().getId().equals(userId)) {
            return ResponseUtil.error("无权删除此帖子", 403);
        }

        // 逻辑删除：将状态设为0
        post.setVisibility("deleted");
        communityPostRepository.save(post);
        return ResponseUtil.success("帖子删除成功（逻辑删除）");
    }

    /**
     * 更新帖子点赞数
     */
    @PutMapping("/{postId}/likes")
    public ResponseEntity<?> updatePostLikes(@PathVariable String postId, @RequestParam Integer count) {
        Optional<CommunityPost> postOpt = communityPostRepository.findById(postId);
        if (postOpt.isPresent()) {
            CommunityPost post = postOpt.get();
            post.setLikesCount(count);
            return ResponseEntity.ok(communityPostRepository.save(post));
        }
        return ResponseUtil.error("帖子不存在", 404);
    }

    /**
     * 更新帖子评论数
     */
    @PutMapping("/{postId}/comments")
    public ResponseEntity<?> updatePostComments(@PathVariable String postId, @RequestParam Integer count) {
        Optional<CommunityPost> postOpt = communityPostRepository.findById(postId);
        if (postOpt.isPresent()) {
            CommunityPost post = postOpt.get();
            post.setCommentsCount(count);
            return ResponseEntity.ok(communityPostRepository.save(post));
        }
        return ResponseUtil.error("帖子不存在", 404);
    }
}
