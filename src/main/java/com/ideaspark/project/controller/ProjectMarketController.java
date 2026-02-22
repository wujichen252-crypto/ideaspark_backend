package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.request.ProjectMarketListRequest;
import com.ideaspark.project.model.dto.response.ProjectMarketDetailResponse;
import com.ideaspark.project.model.dto.response.ProjectMarketListItemResponse;
import com.ideaspark.project.service.ProjectMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/market/projects")
@RequiredArgsConstructor
public class ProjectMarketController {

    private final ProjectMarketService projectMarketService;

    @GetMapping("/list")
    public ResponseEntity<?> listMarketProjects(@ModelAttribute ProjectMarketListRequest request) {
        Page<ProjectMarketListItemResponse> page = projectMarketService.listMarketProjects(request);
        int currentPage = request != null && request.getPage() != null && request.getPage() >= 1
                ? request.getPage()
                : 1;
        Map<String, Object> data = Map.of(
                "projects", page.getContent(),
                "total", page.getTotalElements(),
                "page", currentPage,
                "size", page.getSize()
        );
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", data
        ));
    }

    /**
     * 获取项目市场详情
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getMarketProjectDetail(@PathVariable("projectId") String projectId) {
        ProjectMarketDetailResponse detail = projectMarketService.getMarketProjectDetail(projectId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", detail
        ));
    }
}
