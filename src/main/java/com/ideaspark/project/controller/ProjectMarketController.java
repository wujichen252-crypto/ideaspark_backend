package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.request.ProjectMarketListRequest;
import com.ideaspark.project.model.dto.response.ProjectMarketDetailResponse;
import com.ideaspark.project.model.dto.response.ProjectMarketListItemResponse;
import com.ideaspark.project.service.ProjectMarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 项目市场控制器
 * 提供项目市场展示、搜索、详情查看等公开接口
 */
@RestController
@RequestMapping("/api/market/projects")
@RequiredArgsConstructor
@Tag(name = "项目市场", description = "项目市场展示、搜索、详情查看等公开接口")
public class ProjectMarketController {

    private final ProjectMarketService projectMarketService;

    @GetMapping("/list")
    @Operation(summary = "获取项目市场列表", description = "分页获取项目市场中的公开项目，支持关键词搜索和分类筛选")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<?> listMarketProjects(
            @Parameter(description = "项目查询参数")
            @ModelAttribute ProjectMarketListRequest request) {
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
    @Operation(summary = "获取项目市场详情", description = "根据项目ID获取项目在市场上的详细信息，包括点赞数、标签等")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "项目不存在或不可见")
    })
    public ResponseEntity<?> getMarketProjectDetail(
            @Parameter(description = "项目ID", required = true)
            @PathVariable("projectId") String projectId) {
        ProjectMarketDetailResponse detail = projectMarketService.getMarketProjectDetail(projectId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", detail
        ));
    }
}
