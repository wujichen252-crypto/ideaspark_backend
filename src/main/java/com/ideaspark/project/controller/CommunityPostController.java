package com.ideaspark.project.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaspark.project.model.entity.CommunityPost;
import com.ideaspark.project.model.entity.Project;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.CommunityPostRepository;
import com.ideaspark.project.repository.ProjectRepository;
import com.ideaspark.project.repository.UserRepository;
import com.ideaspark.project.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 社区帖子控制器
 * 提供社区帖子的创建、查询、更新、删除等接口
 */
@RestController
@RequestMapping("/api/community/posts")
@Tag(name = "社区帖子管理", description = "社区帖子的创建、查询、更新、删除等接口")
public class CommunityPostController {

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建帖子
     */
    @PostMapping
    @Operation(summary = "创建帖子", description = "发布新的社区帖子，支持关联项目、添加图片和标签")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<?> createPost(
            @Parameter(description = "帖子数据，包含标题、内容、图片、标签等", required = true)
            @RequestBody Map<String, Object> postData,
            HttpServletRequest request) {
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

            if (postData.get("projectId") != null) {
                String projectId = (String) postData.get("projectId");
                projectRepository.findById(projectId).ifPresent(post::setProject);
            }

            CommunityPost savedPost = communityPostRepository.save(post);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("id", savedPost.getId());
            result.put("title", savedPost.getTitle());
            result.put("content", savedPost.getContent());
            result.put("createdAt", savedPost.getCreatedAt());
            return ResponseUtil.created(result);
        } catch (Exception e) {
            return ResponseUtil.error("创建帖子失败：" + e.getMessage(), 400);
        }
    }

    /**
     * 清理URL字符串，去除反引号、空格、换行符等
     */
    private String cleanUrlString(String url) {
        if (url == null) {
            return "";
        }
        // 去除反引号、空格、换行符、回车符
        return url.replace("`", "")
                  .replace(" ", "")
                  .replace("\n", "")
                  .replace("\r", "")
                  .replace("\t", "")
                  .trim();
    }

    /**
     * 将JSON字符串转换为List
     */
    private List<String> parseJsonStringToList(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            // 去除首尾空格和反引号
            String cleaned = jsonString.trim();
            if (cleaned.startsWith("`") && cleaned.endsWith("`")) {
                cleaned = cleaned.substring(1, cleaned.length() - 1);
            }
            // 解析JSON数组字符串
            List<String> parsedList = objectMapper.readValue(cleaned, new TypeReference<List<String>>() {});
            // 清理每个URL
            return parsedList.stream()
                    .map(this::cleanUrlString)
                    .filter(s -> !s.isEmpty())
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            // 如果解析失败，尝试按逗号分割
            String cleaned = jsonString.trim();
            if (cleaned.startsWith("`") && cleaned.endsWith("`")) {
                cleaned = cleaned.substring(1, cleaned.length() - 1);
            }
            if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
                cleaned = cleaned.substring(1, cleaned.length() - 1);
            }
            List<String> result = new ArrayList<>();
            for (String item : cleaned.split(",")) {
                String trimmed = item.trim();
                // 去除引号
                if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
                    trimmed = trimmed.substring(1, trimmed.length() - 1);
                }
                // 清理URL
                trimmed = cleanUrlString(trimmed);
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
            return result;
        }
    }

    /**
     * 查询所有帖子（公开接口，无需认证）
     */
    @GetMapping
    @Operation(summary = "查询所有帖子", description = "获取所有公开的社区帖子列表，无需登录认证")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    public ResponseEntity<?> getAllPosts(HttpServletRequest request) {
        List<CommunityPost> posts = communityPostRepository.findAll();
        List<Map<String, Object>> result = posts.stream().map(post -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            // 将images和tags从JSON字符串转换为数组
            map.put("images", parseJsonStringToList(post.getImages()));
            map.put("tags", parseJsonStringToList(post.getTags()));
            map.put("channel", post.getChannel());
            map.put("visibility", post.getVisibility());
            map.put("likesCount", post.getLikesCount());
            map.put("commentsCount", post.getCommentsCount());
            map.put("viewsCount", post.getViewsCount());
            map.put("createdAt", post.getCreatedAt());
            map.put("updatedAt", post.getUpdatedAt());
            if (post.getAuthor() != null) {
                Map<String, Object> author = new java.util.HashMap<>();
                author.put("id", post.getAuthor().getId());
                author.put("username", post.getAuthor().getUsername());
                author.put("avatar", post.getAuthor().getAvatar());
                map.put("author", author);
            }
            if (post.getProject() != null) {
                Map<String, Object> project = new java.util.HashMap<>();
                project.put("id", post.getProject().getId());
                project.put("name", post.getProject().getName());
                map.put("project", project);
            }
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseUtil.success(result);
    }

    /**
     * 根据ID查询帖子详情
     */
    @GetMapping("/{postId}")
    @Operation(summary = "获取帖子详情", description = "根据帖子ID获取帖子详细信息，浏览数自动+1")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "帖子不存在")
    })
    public ResponseEntity<?> getPostById(
            @Parameter(description = "帖子ID", required = true)
            @PathVariable String postId) {
        Optional<CommunityPost> postOpt = communityPostRepository.findById(postId);
        if (postOpt.isPresent()) {
            CommunityPost post = postOpt.get();
            // 浏览数+1
            post.setViewsCount(post.getViewsCount() + 1);
            communityPostRepository.save(post);
            
            // 构建返回数据，统一格式
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            // 将images和tags从JSON字符串转换为数组
            map.put("images", parseJsonStringToList(post.getImages()));
            map.put("tags", parseJsonStringToList(post.getTags()));
            map.put("channel", post.getChannel());
            map.put("visibility", post.getVisibility());
            map.put("likesCount", post.getLikesCount());
            map.put("commentsCount", post.getCommentsCount());
            map.put("viewsCount", post.getViewsCount());
            map.put("createdAt", post.getCreatedAt());
            map.put("updatedAt", post.getUpdatedAt());
            if (post.getAuthor() != null) {
                Map<String, Object> author = new java.util.HashMap<>();
                author.put("id", post.getAuthor().getId());
                author.put("username", post.getAuthor().getUsername());
                author.put("avatar", post.getAuthor().getAvatar());
                map.put("author", author);
            }
            if (post.getProject() != null) {
                Map<String, Object> project = new java.util.HashMap<>();
                project.put("id", post.getProject().getId());
                project.put("name", post.getProject().getName());
                map.put("project", project);
            }
            
            return ResponseUtil.success(map);
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
