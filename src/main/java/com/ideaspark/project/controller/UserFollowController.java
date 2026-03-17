package com.ideaspark.project.controller;

import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.model.entity.UserFollow;
import com.ideaspark.project.repository.UserFollowRepository;
import com.ideaspark.project.repository.UserRepository;
import com.ideaspark.project.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 用户关注控制器
 * 接口路径：/api/follows
 */
@RestController
@RequestMapping("/api/follows")
public class UserFollowController {

    @Autowired
    private UserFollowRepository userFollowRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 关注用户
     */
    @PostMapping("/{followingId}")
    public ResponseEntity<?> followUser(@PathVariable Long followingId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        if (currentUserId.equals(followingId)) {
            return ResponseUtil.error("不能关注自己", 400);
        }

        // 检查被关注用户是否存在
        Optional<User> followingUserOpt = userRepository.findById(followingId);
        if (followingUserOpt.isEmpty()) {
            return ResponseUtil.error("被关注的用户不存在", 404);
        }

        Optional<UserFollow> existingFollow = userFollowRepository.findByFollowerIdAndFollowingId(currentUserId, followingId);
        if (existingFollow.isPresent()) {
            return ResponseUtil.error("已关注该用户，无需重复操作", 400);
        }

        UserFollow userFollow = new UserFollow();
        userFollow.setFollower(userRepository.findById(currentUserId).orElse(null));
        userFollow.setFollowing(followingUserOpt.get());
        userFollowRepository.save(userFollow);

        // 更新关注数和粉丝数
        updateFollowCounts(currentUserId);
        updateFollowCounts(followingId);

        return ResponseUtil.success("关注用户成功");
    }

    /**
     * 取消关注用户
     */
    @DeleteMapping("/{followingId}")
    public ResponseEntity<?> unfollowUser(@PathVariable Long followingId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<UserFollow> existingFollow = userFollowRepository.findByFollowerIdAndFollowingId(currentUserId, followingId);
        if (existingFollow.isEmpty()) {
            return ResponseUtil.error("未关注该用户，无需取消", 400);
        }

        userFollowRepository.deleteByFollowerIdAndFollowingId(currentUserId, followingId);

        // 更新关注数和粉丝数
        updateFollowCounts(currentUserId);
        updateFollowCounts(followingId);

        return ResponseUtil.success("取消关注用户成功");
    }

    /**
     * 查询当前用户的关注列表
     */
    @GetMapping("/my/following")
    public ResponseEntity<?> getMyFollowingList(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        List<UserFollow> follows = userFollowRepository.findByFollowerId(currentUserId);
        return ResponseEntity.ok(follows);
    }

    /**
     * 查询当前用户的粉丝列表
     */
    @GetMapping("/my/followers")
    public ResponseEntity<?> getMyFollowerList(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        List<UserFollow> follows = userFollowRepository.findByFollowingId(currentUserId);
        return ResponseEntity.ok(follows);
    }

    /**
     * 查询当前用户的关注数
     */
    @GetMapping("/my/following/count")
    public ResponseEntity<?> getMyFollowingCount(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        long count = userFollowRepository.countByFollowerId(currentUserId);
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    /**
     * 查询当前用户的粉丝数
     */
    @GetMapping("/my/followers/count")
    public ResponseEntity<?> getMyFollowerCount(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        long count = userFollowRepository.countByFollowingId(currentUserId);
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    /**
     * 检查是否已关注某个用户
     */
    @GetMapping("/check/{followingId}")
    public ResponseEntity<?> checkFollow(@PathVariable Long followingId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseEntity.ok(java.util.Map.of("following", false));
        }

        boolean following = userFollowRepository.existsByFollowerIdAndFollowingId(currentUserId, followingId);
        return ResponseEntity.ok(java.util.Map.of("following", following));
    }

    /**
     * 查询指定用户的关注数
     */
    @GetMapping("/user/{userId}/following/count")
    public ResponseEntity<?> getFollowingCountByUserId(@PathVariable Long userId) {
        long count = userFollowRepository.countByFollowerId(userId);
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    /**
     * 查询指定用户的粉丝数
     */
    @GetMapping("/user/{userId}/followers/count")
    public ResponseEntity<?> getFollowerCountByUserId(@PathVariable Long userId) {
        long count = userFollowRepository.countByFollowingId(userId);
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    /**
     * 查询指定用户的关注列表
     */
    @GetMapping("/user/{userId}/following")
    public ResponseEntity<?> getFollowingListByUserId(@PathVariable Long userId) {
        List<UserFollow> follows = userFollowRepository.findByFollowerId(userId);
        return ResponseEntity.ok(follows);
    }

    /**
     * 查询指定用户的粉丝列表
     */
    @GetMapping("/user/{userId}/followers")
    public ResponseEntity<?> getFollowerListByUserId(@PathVariable Long userId) {
        List<UserFollow> follows = userFollowRepository.findByFollowingId(userId);
        return ResponseEntity.ok(follows);
    }

    /**
     * 更新用户的关注数和粉丝数
     */
    private void updateFollowCounts(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFollowingCount((int) userFollowRepository.countByFollowerId(userId));
            user.setFollowersCount((int) userFollowRepository.countByFollowingId(userId));
            userRepository.save(user);
        }
    }
}
