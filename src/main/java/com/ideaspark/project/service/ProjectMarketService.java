package com.ideaspark.project.service;

import com.ideaspark.project.exception.BusinessException;
import com.ideaspark.project.model.dto.request.ProjectMarketListRequest;
import com.ideaspark.project.model.dto.response.ProjectMarketDetailResponse;
import com.ideaspark.project.model.dto.response.ProjectMarketListItemResponse;
import com.ideaspark.project.model.entity.Project;
import com.ideaspark.project.model.entity.Team;
import com.ideaspark.project.model.entity.User;
import com.ideaspark.project.repository.CommunityPostRepository;
import com.ideaspark.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectMarketService {

    private final ProjectRepository projectRepository;

    private final CommunityPostRepository communityPostRepository;

    @Transactional(readOnly = true)
    public Page<ProjectMarketListItemResponse> listMarketProjects(ProjectMarketListRequest request) {
        int page = request != null && request.getPage() != null ? request.getPage() : 1;
        int size = request != null && request.getSize() != null ? request.getSize() : 20;
        if (page < 1) {
            throw new BusinessException("page 不能小于 1");
        }
        if (size <= 0 || size > 200) {
            throw new BusinessException("size 必须在 1~200 之间");
        }
        String keyword = request != null ? normalize(request.getKeyword()) : null;
        String category = request != null ? normalize(request.getCategory()) : null;
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Project> projectPage = projectRepository.searchMarketProjects(keyword, category, pageable);
        List<Project> projects = projectPage.getContent();
        List<String> projectIds = projects.stream()
                .map(Project::getId)
                .filter(Objects::nonNull)
                .toList();
        Map<String, Long> likesMap = new HashMap<>();
        if (!projectIds.isEmpty()) {
            List<Object[]> rows = communityPostRepository.sumLikesByProjectIds(projectIds);
            for (Object[] row : rows) {
                if (row == null || row.length < 2) {
                    continue;
                }
                String projectId = row[0] != null ? String.valueOf(row[0]) : null;
                if (projectId == null) {
                    continue;
                }
                Long likes = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0L;
                likesMap.put(projectId, likes);
            }
        }
        Map<String, List<String>> tagsMap = new HashMap<>();
        if (!projectIds.isEmpty()) {
            List<Object[]> rows = communityPostRepository.findTagsByProjectIds(projectIds);
            for (Object[] row : rows) {
                if (row == null || row.length < 2) {
                    continue;
                }
                String projectId = row[0] != null ? String.valueOf(row[0]) : null;
                String tagsRaw = row[1] != null ? String.valueOf(row[1]) : null;
                if (projectId == null || tagsRaw == null) {
                    continue;
                }
                List<String> parsed = parseTags(tagsRaw);
                if (parsed.isEmpty()) {
                    continue;
                }
                tagsMap.computeIfAbsent(projectId, key -> new ArrayList<>()).addAll(parsed);
            }
        }
        List<ProjectMarketListItemResponse> items = new ArrayList<>();
        for (Project project : projects) {
            ProjectMarketListItemResponse item = new ProjectMarketListItemResponse();
            String projectId = project.getId();
            item.setProjectId(projectId);
            item.setProjectImage(project.getCoverUrl());
            item.setProjectName(project.getName());
            User owner = project.getOwner();
            if (owner != null) {
                item.setOwnerName(owner.getUsername());
                item.setOwnerAvatar(owner.getAvatar());
            }
            item.setLikeCount(projectId != null ? likesMap.getOrDefault(projectId, 0L) : 0L);
            List<String> tagList = new ArrayList<>();
            if (projectId != null && tagsMap.containsKey(projectId)) {
                tagList.addAll(tagsMap.get(projectId));
            }
            String projectCategory = normalize(project.getCategory());
            if (projectCategory != null) {
                tagList.add(projectCategory);
            }
            item.setTags(mergeTags(tagList));
            items.add(item);
        }
        return new PageImpl<>(items, pageable, projectPage.getTotalElements());
    }

    /**
     * 获取项目市场详情
     */
    @Transactional(readOnly = true)
    public ProjectMarketDetailResponse getMarketProjectDetail(String projectId) {
        String normalizedId = normalize(projectId);
        if (normalizedId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        Project project = projectRepository.findByIdAndVisibility(normalizedId, "public")
                .orElseThrow(() -> new BusinessException("项目不存在或不可见"));
        long likeCount = 0L;
        List<Object[]> likeRows = communityPostRepository.sumLikesByProjectIds(List.of(normalizedId));
        if (!likeRows.isEmpty() && likeRows.get(0) != null && likeRows.get(0).length >= 2) {
            Object likeValue = likeRows.get(0)[1];
            if (likeValue instanceof Number) {
                likeCount = ((Number) likeValue).longValue();
            }
        }
        List<String> tagList = new ArrayList<>();
        List<Object[]> tagRows = communityPostRepository.findTagsByProjectIds(List.of(normalizedId));
        for (Object[] row : tagRows) {
            if (row == null || row.length < 2) {
                continue;
            }
            String tagsRaw = row[1] != null ? String.valueOf(row[1]) : null;
            if (tagsRaw == null) {
                continue;
            }
            tagList.addAll(parseTags(tagsRaw));
        }
        String category = normalize(project.getCategory());
        if (category != null) {
            tagList.add(category);
        }
        ProjectMarketDetailResponse dto = new ProjectMarketDetailResponse();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCategory(project.getCategory());
        dto.setCoverUrl(project.getCoverUrl());
        dto.setStatus(project.getStatus());
        dto.setProgress(project.getProgress());
        dto.setVisibility(project.getVisibility());
        dto.setAllowFork(project.getAllowFork());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        User owner = project.getOwner();
        if (owner != null) {
            dto.setOwnerId(owner.getId());
            dto.setOwnerName(owner.getUsername());
            dto.setOwnerAvatar(owner.getAvatar());
        }
        Team team = project.getTeam();
        if (team != null) {
            dto.setTeamId(team.getUuid());
            dto.setTeamName(team.getName());
            dto.setTeamAvatar(team.getAvatarUrl());
            dto.setTeamIsPersonal(team.getIsPersonal());
            dto.setTeamSize(team.getTeamSize());
        }
        dto.setLikeCount(likeCount);
        dto.setTags(mergeTags(tagList));
        return dto;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<String> parseTags(String raw) {
        String normalized = normalize(raw);
        if (normalized == null) {
            return List.of();
        }
        String content = normalized;
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1).trim();
        }
        if (content.isEmpty()) {
            return List.of();
        }
        String[] parts = content.split(",");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            if (part == null) {
                continue;
            }
            String tag = part.trim();
            if ((tag.startsWith("\"") && tag.endsWith("\"")) || (tag.startsWith("'") && tag.endsWith("'"))) {
                tag = tag.substring(1, tag.length() - 1).trim();
            }
            if (!tag.isEmpty()) {
                result.add(tag);
            }
        }
        return result;
    }

    private List<String> mergeTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        Set<String> set = new LinkedHashSet<>();
        for (String tag : tags) {
            String normalized = normalize(tag);
            if (normalized != null) {
                set.add(normalized);
            }
        }
        return new ArrayList<>(set);
    }
}
