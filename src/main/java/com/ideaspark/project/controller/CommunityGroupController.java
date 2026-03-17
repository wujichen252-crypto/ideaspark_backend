package com.ideaspark.project.controller;

import com.ideaspark.project.model.entity.CommunityGroup;
import com.ideaspark.project.model.entity.CommunityGroupMember;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.CommunityGroupMemberRepository;
import com.ideaspark.project.repository.CommunityGroupRepository;
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
 * 社区圈子控制器
 * 接口路径：/api/community/groups
 */
@RestController
@RequestMapping("/api/community/groups")
public class CommunityGroupController {

    @Autowired
    private CommunityGroupRepository groupRepository;

    @Autowired
    private CommunityGroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建圈子
     */
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Map<String, Object> groupData, 
                                         HttpServletRequest request) {
        try {
            Long currentUserId = (Long) request.getAttribute("userId");
            if (currentUserId == null) {
                return ResponseUtil.error("请先登录", 401);
            }

            Optional<User> userOpt = userRepository.findById(currentUserId);
            if (userOpt.isEmpty()) {
                return ResponseUtil.error("用户不存在", 404);
            }

            String name = (String) groupData.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ResponseUtil.error("圈子名称不能为空", 400);
            }

            // 检查圈子名称是否已存在
            if (groupRepository.existsByName(name)) {
                return ResponseUtil.error("圈子名称已存在", 400);
            }

            CommunityGroup group = new CommunityGroup();
            group.setName(name);
            group.setKeyword((String) groupData.get("keyword"));
            group.setDescription((String) groupData.get("description"));
            group.setIconUrl((String) groupData.get("iconUrl"));
            group.setCoverUrl((String) groupData.get("coverUrl"));
            group.setCreatedBy(userOpt.get());

            CommunityGroup savedGroup = groupRepository.save(group);

            // 创建者自动加入圈子并设为管理员
            CommunityGroupMember member = new CommunityGroupMember();
            member.setGroup(savedGroup);
            member.setUser(userOpt.get());
            member.setRole("admin");
            groupMemberRepository.save(member);

            return ResponseUtil.created(savedGroup);
        } catch (Exception e) {
            return ResponseUtil.error("创建圈子失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 查询所有圈子
     */
    @GetMapping
    public ResponseEntity<List<CommunityGroup>> getAllGroups() {
        return ResponseEntity.ok(groupRepository.findAll());
    }

    /**
     * 根据ID查询圈子详情
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@PathVariable String groupId) {
        Optional<CommunityGroup> group = groupRepository.findById(groupId);
        if (group.isEmpty()) {
            return ResponseUtil.error("圈子不存在", 404);
        }
        return ResponseEntity.ok(group.get());
    }

    /**
     * 更新圈子信息
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable String groupId, 
                                         @RequestBody Map<String, Object> groupData,
                                         HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<CommunityGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return ResponseUtil.error("圈子不存在", 404);
        }

        CommunityGroup group = groupOpt.get();
        // 检查是否是创建者
        if (!group.getCreatedBy().getId().equals(currentUserId)) {
            return ResponseUtil.error("无权修改此圈子", 403);
        }

        // 更新字段
        if (groupData.get("name") != null) {
            String newName = (String) groupData.get("name");
            if (!newName.equals(group.getName()) && groupRepository.existsByName(newName)) {
                return ResponseUtil.error("圈子名称已存在", 400);
            }
            group.setName(newName);
        }
        if (groupData.get("keyword") != null) {
            group.setKeyword((String) groupData.get("keyword"));
        }
        if (groupData.get("description") != null) {
            group.setDescription((String) groupData.get("description"));
        }
        if (groupData.get("iconUrl") != null) {
            group.setIconUrl((String) groupData.get("iconUrl"));
        }
        if (groupData.get("coverUrl") != null) {
            group.setCoverUrl((String) groupData.get("coverUrl"));
        }

        return ResponseEntity.ok(groupRepository.save(group));
    }

    /**
     * 删除圈子
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable String groupId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<CommunityGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return ResponseUtil.error("圈子不存在", 404);
        }

        CommunityGroup group = groupOpt.get();
        // 检查是否是创建者
        if (!group.getCreatedBy().getId().equals(currentUserId)) {
            return ResponseUtil.error("无权删除此圈子", 403);
        }

        // 删除圈子（关联的成员记录会自动删除或需要手动删除，取决于数据库配置）
        groupRepository.delete(group);
        return ResponseUtil.success("圈子删除成功");
    }

    /**
     * 加入圈子
     */
    @PostMapping("/{groupId}/join")
    public ResponseEntity<?> joinGroup(@PathVariable String groupId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<CommunityGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return ResponseUtil.error("圈子不存在", 404);
        }

        Optional<User> userOpt = userRepository.findById(currentUserId);
        if (userOpt.isEmpty()) {
            return ResponseUtil.error("用户不存在", 404);
        }

        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            return ResponseUtil.error("已加入该圈子", 400);
        }

        CommunityGroupMember member = new CommunityGroupMember();
        member.setGroup(groupOpt.get());
        member.setUser(userOpt.get());
        member.setRole("member");
        groupMemberRepository.save(member);

        return ResponseUtil.success("加入圈子成功");
    }

    /**
     * 退出圈子
     */
    @DeleteMapping("/{groupId}/join")
    public ResponseEntity<?> leaveGroup(@PathVariable String groupId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        Optional<CommunityGroupMember> memberOpt = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId);
        if (memberOpt.isEmpty()) {
            return ResponseUtil.error("未加入该圈子", 400);
        }

        // 检查是否是创建者
        Optional<CommunityGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent() && groupOpt.get().getCreatedBy().getId().equals(currentUserId)) {
            return ResponseUtil.error("圈子创建者不能退出，请先转让圈子或解散圈子", 400);
        }

        groupMemberRepository.deleteByGroupIdAndUserId(groupId, currentUserId);
        return ResponseUtil.success("退出圈子成功");
    }

    /**
     * 查询圈子成员列表
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getGroupMembers(@PathVariable String groupId) {
        List<CommunityGroupMember> members = groupMemberRepository.findByGroupId(groupId);
        return ResponseEntity.ok(members);
    }

    /**
     * 查询当前用户加入的所有圈子
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyGroups(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        List<CommunityGroupMember> members = groupMemberRepository.findByUserId(currentUserId);
        return ResponseEntity.ok(members);
    }

    /**
     * 查询圈子成员数
     */
    @GetMapping("/{groupId}/members/count")
    public ResponseEntity<?> getGroupMemberCount(@PathVariable String groupId) {
        long count = groupMemberRepository.countByGroupId(groupId);
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }

    /**
     * 检查当前用户是否在指定圈子中
     */
    @GetMapping("/{groupId}/check")
    public ResponseEntity<?> checkMembership(@PathVariable String groupId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseEntity.ok(java.util.Map.of("member", false));
        }

        boolean member = groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId);
        return ResponseEntity.ok(java.util.Map.of("member", member));
    }
}
