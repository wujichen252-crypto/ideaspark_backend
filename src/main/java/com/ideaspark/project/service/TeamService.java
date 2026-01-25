package com.ideaspark.project.service;

import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.request.TeamCreateCollaborationRequest;
import com.ideaspark.project.model.dto.request.TeamDissolveRequest;
import com.ideaspark.project.model.dto.request.TeamMemberListRequest;
import com.ideaspark.project.model.dto.request.TeamMyListRequest;
import com.ideaspark.project.model.dto.request.TeamUpdateRequest;
import com.ideaspark.project.model.dto.response.TeamCreateCollaborationResponse;
import com.ideaspark.project.model.dto.response.TeamDetailResponse;
import com.ideaspark.project.model.dto.response.TeamDissolveResponse;
import com.ideaspark.project.model.dto.response.TeamListItemResponse;
import com.ideaspark.project.model.dto.response.TeamMemberListItemResponse;
import com.ideaspark.project.model.dto.response.TeamUpdateResponse;
import com.ideaspark.project.model.entity.Project;
import com.ideaspark.project.model.entity.Team;
import com.ideaspark.project.model.entity.TeamMember;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.ProjectRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

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
}
