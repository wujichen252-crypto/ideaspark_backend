package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.request.ProjectMyListRequest;
import com.ideaspark.project.model.dto.response.ProjectMyListItemResponse;
import com.ideaspark.project.service.ProjectService;
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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 项目管理控制器
 * 提供项目查询、管理等接口
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "项目管理", description = "项目查询、管理等接口")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/my")
    @Operation(summary = "获取我的项目列表", description = "分页获取当前用户参与的所有项目")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<?> getMyProjects(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "项目查询参数")
            @ModelAttribute ProjectMyListRequest request) {
        Page<ProjectMyListItemResponse> page = projectService.getMyProjects(userId, request);
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
}

