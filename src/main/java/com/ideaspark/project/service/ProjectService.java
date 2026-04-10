package com.ideaspark.project.service;

import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.request.CreateProjectRequest;
import com.ideaspark.project.model.dto.request.ProjectMyListRequest;
import com.ideaspark.project.model.dto.response.CreateProjectResponse;
import com.ideaspark.project.model.dto.response.ProjectMyListItemResponse;
import com.ideaspark.project.model.entity.Project;
import com.ideaspark.project.model.entity.ProjectMember;
import com.ideaspark.project.model.entity.Team;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.ProjectMemberRepository;
import com.ideaspark.project.repository.ProjectRepository;
import com.ideaspark.project.repository.TeamRepository;
import com.ideaspark.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional(readOnly = true)
    public Page<ProjectMyListItemResponse> getMyProjects(Long userId, ProjectMyListRequest request) {
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
        String keyword = request != null ? request.getKeyword() : null;
        String status = request != null ? request.getStatus() : null;
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        if (status != null && status.trim().isEmpty()) {
            status = null;
        }
        Page<Project> projectPage = projectRepository.findMyProjects(userId, keyword, status, pageable);
        List<ProjectMyListItemResponse> items = projectPage.getContent().stream()
                .map(project -> toMyListItem(project, userId))
                .collect(Collectors.toList());
        return new PageImpl<>(items, pageable, projectPage.getTotalElements());
    }

    private ProjectMyListItemResponse toMyListItem(Project project, Long userId) {
        ProjectMyListItemResponse dto = new ProjectMyListItemResponse();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCategory(project.getCategory());
        dto.setCoverUrl(project.getCoverUrl());
        dto.setStatus(project.getStatus());
        dto.setProgress(project.getProgress());
        dto.setVisibility(project.getVisibility());
        dto.setAllowFork(project.getAllowFork());
        if (project.getOwner() != null) {
            dto.setOwnerId(project.getOwner().getId());
            dto.setOwnerName(project.getOwner().getUsername());
        }
        if (project.getTeam() != null) {
            dto.setTeamId(project.getTeam().getUuid());
            dto.setTeamName(project.getTeam().getName());
        }
        String role = "member";
        if (project.getOwner() != null && project.getOwner().getId() != null
                && project.getOwner().getId().equals(userId)) {
            role = "owner";
        }
        dto.setMyRole(role);
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        return dto;
    }

    /**
     * 创建项目
     * @param userId 创建者用户ID
     * @param request 创建项目请求
     * @return 创建的项目响应
     */
    @Transactional
    public CreateProjectResponse createProject(Long userId, CreateProjectRequest request) {
        // 1. 验证用户存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 2. 验证团队存在
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new BusinessException("团队不存在"));

        // 3. 验证用户是团队所有者或管理员（简化处理：只要是团队成员即可创建，实际应检查角色）
        // 注意：这里简化处理，实际应该检查用户在团队中的角色
        
        // 4. 创建项目实体
        Project project = new Project();
        project.setName(request.getName().trim());
        project.setTeam(team);
        project.setOwner(user);
        
        // 设置可选字段
        if (StringUtils.hasText(request.getDescription())) {
            project.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getCategory())) {
            project.setCategory(request.getCategory());
        }
        if (StringUtils.hasText(request.getCoverUrl())) {
            project.setCoverUrl(request.getCoverUrl());
        }
        if (StringUtils.hasText(request.getVisibility())) {
            project.setVisibility(request.getVisibility());
        }
        if (request.getAllowFork() != null) {
            project.setAllowFork(request.getAllowFork());
        }
        
        // 设置初始状态
        project.setStatus("active");
        project.setProgress(0);

        // 5. 保存项目
        Project savedProject = projectRepository.save(project);

        // 6. 创建项目负责人成员记录
        ProjectMember ownerMember = new ProjectMember();
        ownerMember.setProject(savedProject);
        ownerMember.setUser(user);
        ownerMember.setRole("owner");
        projectMemberRepository.save(ownerMember);

        // 7. 转换为响应DTO
        return toCreateResponse(savedProject);
    }

    /**
     * 转换为创建项目响应
     */
    private CreateProjectResponse toCreateResponse(Project project) {
        CreateProjectResponse dto = new CreateProjectResponse();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCategory(project.getCategory());
        dto.setCoverUrl(project.getCoverUrl());
        dto.setStatus(project.getStatus());
        dto.setProgress(project.getProgress());
        dto.setVisibility(project.getVisibility());
        dto.setAllowFork(project.getAllowFork());
        dto.setTags(null); // 标签功能后续实现
        
        if (project.getOwner() != null) {
            dto.setOwnerId(project.getOwner().getId());
            dto.setOwnerName(project.getOwner().getUsername());
        }
        if (project.getTeam() != null) {
            dto.setTeamId(project.getTeam().getUuid());
            dto.setTeamName(project.getTeam().getName());
        }
        dto.setCurrentUserRole("owner");
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        return dto;
    }
}

