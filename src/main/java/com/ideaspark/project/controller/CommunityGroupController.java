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

import java.util.HashMap;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 将CommunityGroup转换为Map，避免Hibernate懒加载问题
     */
    private Map<String, Object> convertGroupToMap(CommunityGroup group) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", group.getId());
        map.put("name", group.getName());
        map.put("keyword", group.getKeyword());
        map.put("description", group.getDescription());
        map.put("iconUrl", group.getIconUrl());
        map.put("coverUrl", group.getCoverUrl());
        map.put("createdAt", group.getCreatedAt());
        map.put("updatedAt", group.getUpdatedAt());

        // 手动转换createdBy，避免懒加载问题
        if (group.getCreatedBy() != null) {
            Map<String, Object> creator = new HashMap<>();
            creator.put("id", group.getCreatedBy().getId());
            creator.put("username", group.getCreatedBy().getUsername());
            creator.put("avatar", group.getCreatedBy().getAvatar());
            map.put("createdBy", creator);
        }

        // 添加成员数量
        long memberCount = groupMemberRepository.countByGroupId(group.getId());
        map.put("memberCount", memberCount);

        return map;
    }

    /**
     * 将CommunityGroupMember转换为Map，避免Hibernate懒加载问题
     */
    private Map<String, Object> convertMemberToMap(CommunityGroupMember member) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", member.getId());
        map.put("role", member.getRole());
        map.put("joinedAt", member.getJoinedAt());

        // 转换用户信息
        if (member.getUser() != null) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", member.getUser().getId());
            user.put("username", member.getUser().getUsername());
            user.put("avatar", member.getUser().getAvatar());
            map.put("user", user);
        }

        // 转换圈子信息
        if (member.getGroup() != null) {
            Map<String, Object> group = new HashMap<>();
            group.put("id", member.getGroup().getId());
            group.put("name", member.getGroup().getName());
            group.put("iconUrl", member.getGroup().getIconUrl());
            group.put("keyword", member.getGroup().getKeyword());
            group.put("description", member.getGroup().getDescription());
            map.put("group", group);
        }

        return map;
    }

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

            return ResponseUtil.created(convertGroupToMap(savedGroup));
        } catch (Exception e) {
            return ResponseUtil.error("创建圈子失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 查询所有圈子
     */
    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        List<CommunityGroup> groups = groupRepository.findAll();
        List<Map<String, Object>> result = groups.stream()
                .map(this::convertGroupToMap)
                .collect(Collectors.toList());
        return ResponseUtil.success(result);
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
        return ResponseUtil.success(convertGroupToMap(group.get()));
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

        CommunityGroup updatedGroup = groupRepository.save(group);
        return ResponseUtil.success(convertGroupToMap(updatedGroup));
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
    @Transactional
    public ResponseEntity<?> leaveGroup(@PathVariable String groupId, HttpServletRequest request) {
        try {
            Long currentUserId = (Long) request.getAttribute("userId");
            if (currentUserId == null) {
                return ResponseUtil.error("请先登录", 401);
            }

            // 检查是否是创建者
            Optional<CommunityGroup> groupOpt = groupRepository.findById(groupId);
            if (groupOpt.isEmpty()) {
                return ResponseUtil.error("圈子不存在", 404);
            }
            
            if (groupOpt.get().getCreatedBy().getId().equals(currentUserId)) {
                return ResponseUtil.error("圈子创建者不能退出，请先转让圈子或解散圈子", 400);
            }

            Optional<CommunityGroupMember> memberOpt = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId);
            if (memberOpt.isEmpty()) {
                return ResponseUtil.error("未加入该圈子", 400);
            }

            // 使用 Repository 的 delete 方法直接删除实体，比自定义 deleteBy 更稳健
            groupMemberRepository.delete(memberOpt.get());
            return ResponseUtil.success("退出圈子成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.error("退出圈子操作失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 查询圈子成员列表
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getGroupMembers(@PathVariable String groupId) {
        List<CommunityGroupMember> members = groupMemberRepository.findByGroupId(groupId);
        List<Map<String, Object>> result = members.stream()
                .map(this::convertMemberToMap)
                .collect(Collectors.toList());
        return ResponseUtil.success(result);
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
        List<Map<String, Object>> result = members.stream()
                .map(this::convertMemberToMap)
                .collect(Collectors.toList());
        return ResponseUtil.success(result);
    }

    /**
     * 查询圈子成员数
     */
    @GetMapping("/{groupId}/members/count")
    public ResponseEntity<?> getGroupMemberCount(@PathVariable String groupId) {
        long count = groupMemberRepository.countByGroupId(groupId);
        return ResponseUtil.success(Map.of("count", count));
    }

    /**
     * 检查当前用户是否在指定圈子中
     */
    @GetMapping("/{groupId}/check")
    public ResponseEntity<?> checkMembership(@PathVariable String groupId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.success(Map.of("member", false));
        }

        boolean member = groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId);
        return ResponseUtil.success(Map.of("member", member));
    }

    /**
     * 移除圈子成员（仅创建者和管理员可调用）
     */
    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<?> removeGroupMember(
            @PathVariable String groupId,
            @PathVariable String memberId,
            HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        // 检查圈子是否存在
        Optional<CommunityGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return ResponseUtil.error("圈子不存在", 404);
        }
        CommunityGroup group = groupOpt.get();

        // 检查当前用户是否有权限（创建者或管理员）
        Optional<CommunityGroupMember> currentMemberOpt = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId);
        boolean isCreator = group.getCreatedBy().getId().equals(currentUserId);
        boolean isAdmin = currentMemberOpt.isPresent() && "admin".equals(currentMemberOpt.get().getRole());
        
        if (!isCreator && !isAdmin) {
            return ResponseUtil.error("无权移除此成员", 403);
        }

        // 检查目标成员是否存在
        Optional<CommunityGroupMember> targetMemberOpt = groupMemberRepository.findById(memberId);
        if (targetMemberOpt.isEmpty() || !targetMemberOpt.get().getGroup().getId().equals(groupId)) {
            return ResponseUtil.error("成员不存在", 404);
        }

        CommunityGroupMember targetMember = targetMemberOpt.get();
        
        // 不能移除创建者
        if (group.getCreatedBy().getId().equals(targetMember.getUser().getId())) {
            return ResponseUtil.error("不能移除圈子创建者", 403);
        }

        // 管理员不能移除其他管理员（只有创建者可以）
        if ("admin".equals(targetMember.getRole()) && !isCreator) {
            return ResponseUtil.error("无权移除管理员", 403);
        }

        groupMemberRepository.delete(targetMember);
        return ResponseUtil.success(Map.of("memberId", memberId, "message", "成员移除成功"));
    }

    /**
     * 更新圈子成员角色（仅创建者可调用）
     */
    @PutMapping("/{groupId}/members/{memberId}/role")
    public ResponseEntity<?> updateGroupMemberRole(
            @PathVariable String groupId,
            @PathVariable String memberId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseUtil.error("请先登录", 401);
        }

        // 检查圈子是否存在
        Optional<CommunityGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return ResponseUtil.error("圈子不存在", 404);
        }
        CommunityGroup group = groupOpt.get();

        // 检查当前用户是否是创建者
        if (!group.getCreatedBy().getId().equals(currentUserId)) {
            return ResponseUtil.error("只有圈子创建者可以修改成员角色", 403);
        }

        // 检查目标成员是否存在
        Optional<CommunityGroupMember> targetMemberOpt = groupMemberRepository.findById(memberId);
        if (targetMemberOpt.isEmpty() || !targetMemberOpt.get().getGroup().getId().equals(groupId)) {
            return ResponseUtil.error("成员不存在", 404);
        }

        CommunityGroupMember targetMember = targetMemberOpt.get();
        
        // 不能修改创建者的角色
        if (group.getCreatedBy().getId().equals(targetMember.getUser().getId())) {
            return ResponseUtil.error("不能修改创建者的角色", 403);
        }

        String newRole = body.get("role");
        if (newRole == null || (!"admin".equals(newRole) && !"member".equals(newRole))) {
            return ResponseUtil.error("角色必须是 admin 或 member", 400);
        }

        String oldRole = targetMember.getRole();
        targetMember.setRole(newRole);
        groupMemberRepository.save(targetMember);

        return ResponseUtil.success(Map.of(
                "memberId", memberId,
                "oldRole", oldRole,
                "newRole", newRole,
                "message", "角色更新成功"
        ));
    }
}
