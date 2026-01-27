package com.ideaspark.project.service;

import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.request.TeamCreateCollaborationRequest;
import com.ideaspark.project.model.dto.request.TeamDissolveRequest;
import com.ideaspark.project.model.dto.request.TeamInvitationSendRequest;
import com.ideaspark.project.model.dto.request.TeamMemberListRequest;
import com.ideaspark.project.model.dto.request.TeamMemberRoleUpdateRequest;
import com.ideaspark.project.model.dto.request.TeamMyListRequest;
import com.ideaspark.project.model.dto.request.TeamTransferOwnershipRequest;
import com.ideaspark.project.model.dto.request.TeamUpdateRequest;
import com.ideaspark.project.model.dto.response.TeamCreateCollaborationResponse;
import com.ideaspark.project.model.dto.response.TeamDetailResponse;
import com.ideaspark.project.model.dto.response.TeamDissolveResponse;
import com.ideaspark.project.model.dto.response.TeamInvitationItemResponse;
import com.ideaspark.project.model.dto.response.TeamInvitationSendResponse;
import com.ideaspark.project.model.dto.response.TeamListItemResponse;
import com.ideaspark.project.model.dto.response.TeamMemberListItemResponse;
import com.ideaspark.project.model.dto.response.TeamMemberRoleUpdateResponse;
import com.ideaspark.project.model.dto.response.TeamMemberRemoveResponse;
import com.ideaspark.project.model.dto.response.TeamExitResponse;
import com.ideaspark.project.model.dto.response.TeamTransferOwnershipResponse;
import com.ideaspark.project.model.dto.response.TeamUpdateResponse;
import com.ideaspark.project.model.entity.Project;
import com.ideaspark.project.model.entity.Team;
import com.ideaspark.project.model.entity.TeamInvitation;
import com.ideaspark.project.model.entity.TeamMember;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.ProjectRepository;
import com.ideaspark.project.repository.TeamInvitationRepository;
import com.ideaspark.project.repository.TeamMemberRepository;
import com.ideaspark.project.repository.TeamRepository;
import com.ideaspark.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    private final TeamInvitationRepository teamInvitationRepository;

    @Transactional
    public TeamCreateCollaborationResponse createCollaborationTeam(Long userId, TeamCreateCollaborationRequest request) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BusinessException("团队名称不能为空");
        }
        String name = request.getName().trim();
        if (teamRepository.existsByName(name)) {
            throw new BusinessException("团队名称已存在");
        }
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        Team team = new Team();
        team.setName(name);
        team.setAvatarUrl(request.getAvatarUrl());
        team.setDescription(request.getDescription());
        team.setIsPersonal(false);
        team.setOwner(owner);
        team.setTeamSize(1);
        Team savedTeam = teamRepository.save(team);
        TeamMember ownerMember = new TeamMember();
        ownerMember.setTeam(savedTeam);
        ownerMember.setUser(owner);
        ownerMember.setRole("owner");
        teamMemberRepository.save(ownerMember);
        TeamDetailResponse teamDto = new TeamDetailResponse();
        teamDto.setUuid(savedTeam.getUuid());
        teamDto.setName(savedTeam.getName());
        teamDto.setAvatarUrl(savedTeam.getAvatarUrl());
        teamDto.setDescription(savedTeam.getDescription());
        teamDto.setIsPersonal(savedTeam.getIsPersonal());
        teamDto.setTeamType("协作团队");
        teamDto.setOwnerId(owner.getId());
        teamDto.setOwnerName(owner.getUsername());
        teamDto.setCreatedAt(savedTeam.getCreatedAt());
        teamDto.setUpdatedAt(savedTeam.getUpdatedAt());
        teamDto.setCurrentUserRole("owner");
        teamDto.setTeamSize(1L);
        TeamCreateCollaborationResponse response = new TeamCreateCollaborationResponse();
        response.setTeam(teamDto);
        return response;
    }

    @Transactional
    public TeamDissolveResponse dissolveTeam(String teamUuid, Long userId, TeamDissolveRequest request) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (teamUuid == null || teamUuid.trim().isEmpty()) {
            throw new BusinessException("团队UUID不能为空");
        }
        Team team = teamRepository.findById(teamUuid)
                .orElseThrow(() -> new BusinessException("团队不存在"));
        Optional<TeamMember> membershipOpt = teamMemberRepository.findByTeam_UuidAndUser_Id(teamUuid, userId);
        TeamMember membership = membershipOpt.orElseThrow(() -> new BusinessException("无权访问该团队"));
        String role = membership.getRole();
        if (role == null || !"owner".equalsIgnoreCase(role)) {
            throw new BusinessException("仅团队所有者可以解散团队");
        }
        String reason = request != null ? request.getReason() : null;
        team.setDissolvedAt(LocalDateTime.now());
        team.setTeamSize(0);
        Team saved = teamRepository.save(team);
        TeamDissolveResponse response = new TeamDissolveResponse();
        response.setTeamId(saved.getUuid());
        response.setTeamName(saved.getName());
        response.setDissolvedAt(saved.getDissolvedAt());
        response.setReason(reason);
        return response;
    }

    @Transactional(readOnly = true)
    public TeamDetailResponse getTeamDetail(String teamUuid, Long userId) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (teamUuid == null || teamUuid.trim().isEmpty()) {
            throw new BusinessException("团队UUID不能为空");
        }
        Team team = teamRepository.findById(teamUuid)
                .orElseThrow(() -> new BusinessException("团队不存在"));
        Optional<TeamMember> membershipOpt = teamMemberRepository.findByTeam_UuidAndUser_Id(teamUuid, userId);
        TeamMember membership = membershipOpt.orElseThrow(() -> new BusinessException("无权访问该团队"));
        TeamDetailResponse dto = new TeamDetailResponse();
        dto.setName(team.getName());
        dto.setAvatarUrl(team.getAvatarUrl());
        dto.setDescription(team.getDescription());
        dto.setIsPersonal(team.getIsPersonal());
        dto.setTeamType(Boolean.TRUE.equals(team.getIsPersonal()) ? "个人团队" : "协作团队");
        if (team.getOwner() != null) {
            dto.setOwnerId(team.getOwner().getId());
            dto.setOwnerName(team.getOwner().getUsername());
        }
        dto.setCreatedAt(team.getCreatedAt());
        dto.setUpdatedAt(team.getUpdatedAt());
        dto.setCurrentUserRole(membership.getRole());
        long count = teamMemberRepository.countByTeam_Uuid(teamUuid);
        dto.setTeamSize(count);
        Optional<Project> projectOpt = projectRepository.findFirstByTeam_UuidOrderByCreatedAtDesc(teamUuid);
        projectOpt.ifPresent(project -> dto.setProjectId(project.getId()));
        dto.setUuid(team.getUuid());
        return dto;
    }

    @Transactional
    public TeamUpdateResponse updateTeam(String teamUuid, Long userId, TeamUpdateRequest request) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (teamUuid == null || teamUuid.trim().isEmpty()) {
            throw new BusinessException("团队UUID不能为空");
        }
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        Team team = teamRepository.findById(teamUuid)
                .orElseThrow(() -> new BusinessException("团队不存在"));
        Optional<TeamMember> membershipOpt = teamMemberRepository.findByTeam_UuidAndUser_Id(teamUuid, userId);
        TeamMember membership = membershipOpt.orElseThrow(() -> new BusinessException("无权访问该团队"));
        String role = membership.getRole();
        if (role == null || (!"owner".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role))) {
            throw new BusinessException("无权限更新团队信息");
        }
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            team.setName(request.getName().trim());
        }
        if (request.getAvatarUrl() != null) {
            team.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getDescription() != null) {
            team.setDescription(request.getDescription());
        }
        Team saved = teamRepository.save(team);
        TeamUpdateResponse response = new TeamUpdateResponse();
        response.setUuid(saved.getUuid());
        response.setName(saved.getName());
        response.setAvatarUrl(saved.getAvatarUrl());
        response.setDescription(saved.getDescription());
        response.setUpdatedAt(saved.getUpdatedAt());
        return response;
    }

    @Transactional(readOnly = true)
    public Page<TeamListItemResponse> getMyTeams(Long userId, TeamMyListRequest request) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        int page = request != null && request.getPage() != null ? request.getPage() : 1;
        int size = request != null && request.getSize() != null ? request.getSize() : 20;
        if (page < 1) {
            throw new BusinessException("page 不能小于 1");
        }
        if (size <= 0 || size > 200) {
            throw new BusinessException("size 必须在 1~200 之间");
        }
        int pageIndex = page - 1;
        Pageable pageable = PageRequest.of(pageIndex, size);
        Page<TeamMember> membershipPage = teamMemberRepository.findByUser_Id(userId, pageable);
        List<TeamListItemResponse> items = membershipPage.getContent().stream()
                .map(this::toTeamListItem)
                .collect(Collectors.toList());
        return new PageImpl<>(items, pageable, membershipPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<TeamMemberListItemResponse> getTeamMembers(String teamUuid, Long userId, TeamMemberListRequest request) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (teamUuid == null || teamUuid.trim().isEmpty()) {
            throw new BusinessException("团队UUID不能为空");
        }
        Team team = teamRepository.findById(teamUuid)
                .orElseThrow(() -> new BusinessException("团队不存在"));
        Optional<TeamMember> membershipOpt = teamMemberRepository.findByTeam_UuidAndUser_Id(teamUuid, userId);
        TeamMember membership = membershipOpt.orElseThrow(() -> new BusinessException("无权访问该团队"));
        String currentRole = membership.getRole();
        int page = request != null && request.getPage() != null ? request.getPage() : 1;
        int size = request != null && request.getSize() != null ? request.getSize() : 20;
        if (page < 1) {
            throw new BusinessException("page 不能小于 1");
        }
        if (size <= 0 || size > 200) {
            throw new BusinessException("size 必须在 1~200 之间");
        }
        int pageIndex = page - 1;
        Pageable pageable = PageRequest.of(pageIndex, size);
        String roleFilter = request != null ? request.getRole() : null;
        String keyword = request != null ? request.getKeyword() : null;
        boolean hasRole = roleFilter != null && !roleFilter.trim().isEmpty();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        Page<TeamMember> memberPage;
        if (hasRole && hasKeyword) {
            memberPage = teamMemberRepository.findByTeam_UuidAndRoleAndUser_UsernameContainingIgnoreCase(teamUuid, roleFilter, keyword, pageable);
        } else if (hasRole) {
            memberPage = teamMemberRepository.findByTeam_UuidAndRole(teamUuid, roleFilter, pageable);
        } else if (hasKeyword) {
            memberPage = teamMemberRepository.findByTeam_UuidAndUser_UsernameContainingIgnoreCase(teamUuid, keyword, pageable);
        } else {
            memberPage = teamMemberRepository.findByTeam_Uuid(teamUuid, pageable);
        }
        List<TeamMemberListItemResponse> items = memberPage.getContent().stream()
                .map(member -> toTeamMemberListItem(member, currentRole, userId))
                .collect(Collectors.toList());
        return new PageImpl<>(items, pageable, memberPage.getTotalElements());
    }

    @Transactional
    public TeamMemberRoleUpdateResponse updateMemberRole(String teamUuid, String memberId, Long userId, TeamMemberRoleUpdateRequest request) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (teamUuid == null || teamUuid.trim().isEmpty()) {
            throw new BusinessException("团队UUID不能为空");
        }
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new BusinessException("成员ID不能为空");
        }
        if (request == null || request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new BusinessException("角色不能为空");
        }
        Team team = teamRepository.findById(teamUuid)
                .orElseThrow(() -> new BusinessException("团队不存在"));
        Optional<TeamMember> membershipOpt = teamMemberRepository.findByTeam_UuidAndUser_Id(teamUuid, userId);
        TeamMember currentMembership = membershipOpt.orElseThrow(() -> new BusinessException("无权访问该团队"));
        TeamMember targetMember = teamMemberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new BusinessException("成员不存在"));
        if (targetMember.getTeam() == null || !teamUuid.equals(targetMember.getTeam().getUuid())) {
            throw new BusinessException("成员不属于当前团队");
        }
        String currentRole = currentMembership.getRole();
        if (!canChangeMemberRole(currentRole, targetMember, userId)) {
            throw new BusinessException("无权限修改该成员角色");
        }
        String newRole = request.getRole().trim().toLowerCase();
        if ("owner".equalsIgnoreCase(newRole)) {
            throw new BusinessException("不能将角色修改为所有者");
        }
        if (!"admin".equalsIgnoreCase(newRole)
                && !"member".equalsIgnoreCase(newRole)
                && !"visitor".equalsIgnoreCase(newRole)) {
            throw new BusinessException("角色不合法");
        }
        String oldRole = targetMember.getRole();
        targetMember.setRole(newRole);
        teamMemberRepository.save(targetMember);
        TeamMemberRoleUpdateResponse response = new TeamMemberRoleUpdateResponse();
        response.setMemberId(targetMember.getId());
        User user = targetMember.getUser();
        if (user != null) {
            response.setUserId(user.getId());
            response.setUserName(user.getUsername());
        }
        response.setOldRole(oldRole);
        response.setNewRole(newRole);
        return response;
    }

    @Transactional
    public TeamMemberRemoveResponse removeMember(String teamUuid, String memberId, Long userId) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (teamUuid == null || teamUuid.trim().isEmpty()) {
            throw new BusinessException("团队UUID不能为空");
        }
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new BusinessException("成员ID不能为空");
        }
        Team team = teamRepository.findById(teamUuid)
                .orElseThrow(() -> new BusinessException("团队不存在"));
        Optional<TeamMember> membershipOpt = teamMemberRepository.findByTeam_UuidAndUser_Id(teamUuid, userId);
        TeamMember currentMembership = membershipOpt.orElseThrow(() -> new BusinessException("无权访问该团队"));
        TeamMember targetMember = teamMemberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new BusinessException("成员不存在"));
        if (targetMember.getTeam() == null || !teamUuid.equals(targetMember.getTeam().getUuid())) {
            throw new BusinessException("成员不属于当前团队");
        }
        String currentRole = currentMembership.getRole();
        if (!canRemoveMember(currentRole, targetMember, userId)) {
            throw new BusinessException("无权限移除该成员");
        }
        User targetUser = targetMember.getUser();
        TeamMemberRemoveResponse response = new TeamMemberRemoveResponse();
        response.setMemberId(targetMember.getId());
        if (targetUser != null) {
            response.setUserId(targetUser.getId());
            response.setUserName(targetUser.getUsername());
        }
        teamMemberRepository.delete(targetMember);
        return response;
    }

    @Transactional
    public TeamExitResponse exitTeam(String teamUuid, Long userId) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (teamUuid == null || teamUuid.trim().isEmpty()) {
            throw new BusinessException("团队UUID不能为空");
        }
        Team team = teamRepository.findById(teamUuid)
                .orElseThrow(() -> new BusinessException("团队不存在"));
        Optional<TeamMember> membershipOpt = teamMemberRepository.findByTeam_UuidAndUser_Id(teamUuid, userId);
        TeamMember membership = membershipOpt.orElseThrow(() -> new BusinessException("无权访问该团队"));
        String role = membership.getRole();
        if (role == null) {
            throw new BusinessException("无权限退出团队");
        }
        if ("owner".equalsIgnoreCase(role)) {
            if (Boolean.TRUE.equals(team.getIsPersonal())) {
                throw new BusinessException("个人团队不能退出");
            }
            throw new BusinessException("团队所有者不能退出团队");
        }
        if (!"admin".equalsIgnoreCase(role) && !"member".equalsIgnoreCase(role)) {
            throw new BusinessException("无权限退出团队");
        }
        User user = membership.getUser();
        teamMemberRepository.delete(membership);
        TeamExitResponse response = new TeamExitResponse();
        response.setTeamId(team.getUuid());
        response.setTeamName(team.getName());
        if (user != null) {
            response.setUserId(user.getId());
            response.setUserName(user.getUsername());
        }
        return response;
    }

    @Transactional
    public TeamTransferOwnershipResponse transferOwnership(String teamUuid, Long userId, TeamTransferOwnershipRequest request) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (teamUuid == null || teamUuid.trim().isEmpty()) {
            throw new BusinessException("团队UUID不能为空");
        }
        if (request == null || request.getNewOwnerMemberId() == null || request.getNewOwnerMemberId().trim().isEmpty()) {
            throw new BusinessException("新所有者成员ID不能为空");
        }
        Team team = teamRepository.findById(teamUuid)
                .orElseThrow(() -> new BusinessException("团队不存在"));
        if (Boolean.TRUE.equals(team.getIsPersonal())) {
            throw new BusinessException("个人团队不支持转让所有权");
        }
        Optional<TeamMember> membershipOpt = teamMemberRepository.findByTeam_UuidAndUser_Id(teamUuid, userId);
        TeamMember currentMembership = membershipOpt.orElseThrow(() -> new BusinessException("无权访问该团队"));
        String currentRole = currentMembership.getRole();
        if (currentRole == null || !"owner".equalsIgnoreCase(currentRole)) {
            throw new BusinessException("仅团队所有者可以转让所有权");
        }
        User oldOwner = currentMembership.getUser();
        if (oldOwner == null || team.getOwner() == null || !userId.equals(team.getOwner().getId())) {
            throw new BusinessException("仅当前团队所有者可以转让所有权");
        }
        Long newOwnerMemberId;
        try {
            newOwnerMemberId = Long.valueOf(request.getNewOwnerMemberId());
        } catch (NumberFormatException e) {
            throw new BusinessException("新所有者成员ID不合法");
        }
        TeamMember targetMember = teamMemberRepository.findById(newOwnerMemberId)
                .orElseThrow(() -> new BusinessException("目标成员不存在"));
        if (targetMember.getTeam() == null || !teamUuid.equals(targetMember.getTeam().getUuid())) {
            throw new BusinessException("目标成员不属于当前团队");
        }
        if (targetMember.getUser() == null) {
            throw new BusinessException("目标成员用户信息异常");
        }
        String targetRole = targetMember.getRole();
        if (targetRole == null || !"admin".equalsIgnoreCase(targetRole)) {
            throw new BusinessException("仅可将所有者转让给管理员");
        }
        User newOwner = targetMember.getUser();
        team.setOwner(newOwner);
        currentMembership.setRole("admin");
        targetMember.setRole("owner");
        teamRepository.save(team);
        teamMemberRepository.save(currentMembership);
        teamMemberRepository.save(targetMember);
        TeamTransferOwnershipResponse response = new TeamTransferOwnershipResponse();
        response.setTeamId(team.getUuid());
        response.setOldOwnerId(oldOwner.getId());
        response.setNewOwnerId(newOwner.getId());
        return response;
    }

    @Transactional
    public TeamInvitationSendResponse sendInvitations(String teamUuid, Long userId, TeamInvitationSendRequest request) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        if (teamUuid == null || teamUuid.trim().isEmpty()) {
            throw new BusinessException("团队UUID不能为空");
        }
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        String type = request.getType() != null ? request.getType().trim().toLowerCase() : null;
        if (type == null || type.isEmpty()) {
            throw new BusinessException("邀请方式不能为空");
        }
        if (!"link".equals(type) && !"email".equals(type)) {
            throw new BusinessException("邀请方式不合法");
        }
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new BusinessException("邀请角色不能为空");
        }
        Team team = teamRepository.findById(teamUuid)
                .orElseThrow(() -> new BusinessException("团队不存在"));
        Optional<TeamMember> membershipOpt = teamMemberRepository.findByTeam_UuidAndUser_Id(teamUuid, userId);
        TeamMember membership = membershipOpt.orElseThrow(() -> new BusinessException("无权访问该团队"));
        String currentRole = membership.getRole();
        if (currentRole == null || (!"owner".equalsIgnoreCase(currentRole) && !"admin".equalsIgnoreCase(currentRole))) {
            throw new BusinessException("无权限发送团队邀请");
        }
        String role = request.getRole().trim().toLowerCase();
        if (!"admin".equalsIgnoreCase(role) && !"member".equalsIgnoreCase(role) && !"visitor".equalsIgnoreCase(role)) {
            throw new BusinessException("邀请角色不合法");
        }
        if ("admin".equalsIgnoreCase(role) && !"owner".equalsIgnoreCase(currentRole)) {
            throw new BusinessException("仅团队所有者可以邀请管理员");
        }
        if ("link".equals(type)) {
            User inviter = membership.getUser();
            TeamInvitation invitation = new TeamInvitation();
            invitation.setTeam(team);
            invitation.setInviter(inviter);
            invitation.setRole(role);
            invitation.setToken(generateInvitationToken());
            invitation.setStatus("PENDING");
            invitation.setExpiresAt(LocalDateTime.now().plusDays(7));
            TeamInvitation saved = teamInvitationRepository.save(invitation);
            TeamInvitationItemResponse dto = new TeamInvitationItemResponse();
            dto.setRole(role);
            dto.setStatus(saved.getStatus());
            dto.setToken(saved.getToken());
            dto.setExpiresAt(saved.getExpiresAt());
            List<TeamInvitationItemResponse> invitationDtos = List.of(dto);
            TeamInvitationSendResponse response = new TeamInvitationSendResponse();
            response.setTotalInvited(1);
            response.setSuccessCount(1);
            response.setInvitations(invitationDtos);
            return response;
        }
        List<String> emails = request.getEmails();
        if (emails == null || emails.isEmpty()) {
            throw new BusinessException("邮箱列表不能为空");
        }
        User inviter = membership.getUser();
        int totalInvited = emails.size();
        int successCount = 0;
        List<TeamInvitationItemResponse> invitationDtos = new ArrayList<>();
        for (String rawEmail : emails) {
            if (rawEmail == null) {
                continue;
            }
            String email = rawEmail.trim();
            if (email.isEmpty()) {
                continue;
            }
            User inviteeUser = null;
            Optional<User> userByEmailOpt = userRepository.findByEmail(email);
            if (userByEmailOpt.isPresent()) {
                User user = userByEmailOpt.get();
                boolean isMember = teamMemberRepository.findByTeam_UuidAndUser_Id(teamUuid, user.getId()).isPresent();
                if (isMember) {
                    continue;
                }
                inviteeUser = user;
            }
            TeamInvitation invitation = new TeamInvitation();
            invitation.setTeam(team);
            invitation.setInviter(inviter);
            invitation.setInvitee(inviteeUser);
            invitation.setInviteeEmail(email);
            invitation.setRole(role);
            invitation.setToken(generateInvitationToken());
            invitation.setStatus("PENDING");
            invitation.setExpiresAt(LocalDateTime.now().plusDays(7));
            TeamInvitation saved = teamInvitationRepository.save(invitation);
            TeamInvitationItemResponse dto = new TeamInvitationItemResponse();
            if (saved.getInvitee() != null) {
                dto.setInviteeId(saved.getInvitee().getId());
            }
            dto.setInviteeEmail(saved.getInviteeEmail());
            dto.setRole(role);
            dto.setStatus(saved.getStatus());
            dto.setToken(saved.getToken());
            dto.setExpiresAt(saved.getExpiresAt());
            invitationDtos.add(dto);
            successCount++;
        }
        TeamInvitationSendResponse response = new TeamInvitationSendResponse();
        response.setTotalInvited(totalInvited);
        response.setSuccessCount(successCount);
        response.setInvitations(invitationDtos);
        return response;
    }

    private TeamListItemResponse toTeamListItem(TeamMember membership) {
        Team team = membership.getTeam();
        if (team == null) {
            throw new BusinessException("团队数据异常");
        }
        TeamListItemResponse dto = new TeamListItemResponse();
        dto.setName(team.getName());
        dto.setAvatarUrl(team.getAvatarUrl());
        return dto;
    }

    private TeamMemberListItemResponse toTeamMemberListItem(TeamMember member, String currentRole, Long currentUserId) {
        if (member == null) {
            throw new BusinessException("成员数据异常");
        }
        User user = member.getUser();
        TeamMemberListItemResponse dto = new TeamMemberListItemResponse();
        dto.setId(member.getId());
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUserName(user.getUsername());
            dto.setUserAvatar(user.getAvatar());
        }
        dto.setRole(member.getRole());
        dto.setRoleCn(teamRoleToCn(member.getRole()));
        dto.setJoinedAt(member.getJoinedAt());
        dto.setCanRemove(canRemoveMember(currentRole, member, currentUserId));
        dto.setCanChangeRole(canChangeMemberRole(currentRole, member, currentUserId));
        return dto;
    }

    private String teamRoleToCn(String role) {
        if (role == null) {
            return null;
        }
        if ("owner".equalsIgnoreCase(role)) {
            return "所有者";
        }
        if ("admin".equalsIgnoreCase(role)) {
            return "管理员";
        }
        if ("member".equalsIgnoreCase(role)) {
            return "成员";
        }
        if ("visitor".equalsIgnoreCase(role)) {
            return "访客";
        }
        return role;
    }

    private boolean canRemoveMember(String currentRole, TeamMember targetMember, Long currentUserId) {
        if (targetMember == null || currentUserId == null) {
            return false;
        }
        User targetUser = targetMember.getUser();
        if (targetUser != null && currentUserId.equals(targetUser.getId())) {
            return false;
        }
        String targetRole = targetMember.getRole();
        if (targetRole != null && "owner".equalsIgnoreCase(targetRole)) {
            return false;
        }
        if (currentRole == null) {
            return false;
        }
        if ("owner".equalsIgnoreCase(currentRole)) {
            return true;
        }
        if ("admin".equalsIgnoreCase(currentRole)) {
            return "member".equalsIgnoreCase(targetRole) || "visitor".equalsIgnoreCase(targetRole);
        }
        return false;
    }

    private boolean canChangeMemberRole(String currentRole, TeamMember targetMember, Long currentUserId) {
        if (targetMember == null || currentUserId == null) {
            return false;
        }
        User targetUser = targetMember.getUser();
        if (targetUser != null && currentUserId.equals(targetUser.getId())) {
            return false;
        }
        String targetRole = targetMember.getRole();
        if (currentRole == null) {
            return false;
        }
        if ("owner".equalsIgnoreCase(currentRole)) {
            return targetRole == null || !"owner".equalsIgnoreCase(targetRole);
        }
        if ("admin".equalsIgnoreCase(currentRole)) {
            return "member".equalsIgnoreCase(targetRole) || "visitor".equalsIgnoreCase(targetRole);
        }
        return false;
    }

    @Transactional
    public Map<String, Object> validateInvitationToken(String token, Long userId) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        String trimmedToken = token != null ? token.trim() : null;
        if (trimmedToken == null || trimmedToken.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("valid", false);
            data.put("reason", "INVALID_TOKEN");
            return data;
        }
        Optional<TeamInvitation> invitationOpt = teamInvitationRepository.findFirstByToken(trimmedToken);
        if (invitationOpt.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("valid", false);
            data.put("reason", "EXPIRED");
            return data;
        }
        TeamInvitation invitation = invitationOpt.get();
        Team team = invitation.getTeam();
        if (team == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("valid", false);
            data.put("reason", "EXPIRED");
            return data;
        }
        LocalDateTime now = LocalDateTime.now();
        if (invitation.getExpiresAt() != null && invitation.getExpiresAt().isBefore(now)) {
            Map<String, Object> data = new HashMap<>();
            data.put("valid", false);
            data.put("reason", "EXPIRED");
            return data;
        }
        if (invitation.getStatus() != null && !"PENDING".equalsIgnoreCase(invitation.getStatus())) {
            Map<String, Object> data = new HashMap<>();
            data.put("valid", false);
            data.put("reason", "EXPIRED");
            return data;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        boolean alreadyMember = teamMemberRepository.findByTeam_UuidAndUser_Id(team.getUuid(), user.getId()).isPresent();
        boolean joined = false;
        if (!alreadyMember) {
            String invitationRole = invitation.getRole();
            String memberRole;
            if (invitationRole != null && !invitationRole.trim().isEmpty()) {
                String r = invitationRole.trim().toLowerCase();
                if ("admin".equals(r) || "member".equals(r) || "visitor".equals(r)) {
                    memberRole = r;
                } else {
                    memberRole = "member";
                }
            } else {
                memberRole = "member";
            }
            TeamMember member = new TeamMember();
            member.setTeam(team);
            member.setUser(user);
            member.setRole(memberRole);
            teamMemberRepository.save(member);
            Integer size = team.getTeamSize();
            if (size == null) {
                size = 0;
            }
            team.setTeamSize(size + 1);
            teamRepository.save(team);
            joined = true;
        }
        invitation.setStatus("ACCEPTED");
        teamInvitationRepository.save(invitation);
        User inviter = invitation.getInviter();
        Map<String, Object> invitationInfo = new HashMap<>();
        if (team != null) {
            invitationInfo.put("teamId", team.getUuid());
            invitationInfo.put("teamName", team.getName());
            invitationInfo.put("teamAvatar", team.getAvatarUrl());
        }
        if (inviter != null) {
            invitationInfo.put("inviterName", inviter.getUsername());
        }
        invitationInfo.put("role", invitation.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("valid", true);
        data.put("invitation", invitationInfo);
        data.put("joined", joined);
        return data;
    }

    private String generateInvitationToken() {
        String part1 = UUID.randomUUID().toString().replace("-", "");
        String part2 = UUID.randomUUID().toString().replace("-", "");
        return part1 + part2;
    }
}
