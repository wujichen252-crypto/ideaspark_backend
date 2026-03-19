package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.request.ProjectMyListRequest;
import com.ideaspark.project.model.dto.response.ProjectMyListItemResponse;
import com.ideaspark.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyProjects(@RequestAttribute("userId") Long userId,
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

