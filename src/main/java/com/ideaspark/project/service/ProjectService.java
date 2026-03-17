package com.ideaspark.project.service;

import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.request.ProjectMyListRequest;
import com.ideaspark.project.model.dto.response.ProjectMyListItemResponse;
import com.ideaspark.project.model.entity.Project;
import com.ideaspark.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

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
}

