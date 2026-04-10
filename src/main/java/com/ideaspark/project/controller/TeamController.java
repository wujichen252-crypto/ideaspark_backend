package com.ideaspark.project.controller;

import com.ideaspark.project.model.dto.request.TeamCreateCollaborationRequest;
import com.ideaspark.project.model.dto.request.TeamDissolveRequest;
import com.ideaspark.project.model.dto.request.TeamInvitationSendRequest;
import com.ideaspark.project.model.dto.request.TeamMemberListRequest;
import com.ideaspark.project.model.dto.request.TeamMemberRoleUpdateRequest;
import com.ideaspark.project.model.dto.request.TeamMyListRequest;
import com.ideaspark.project.model.dto.request.TeamUpdateRequest;
import com.ideaspark.project.model.dto.request.TeamTransferOwnershipRequest;
import com.ideaspark.project.model.dto.response.TeamCreateCollaborationResponse;
import com.ideaspark.project.model.dto.response.TeamDetailResponse;
import com.ideaspark.project.model.dto.response.TeamDissolveResponse;
import com.ideaspark.project.model.dto.response.TeamInvitationSendResponse;
import com.ideaspark.project.model.dto.response.TeamListItemResponse;
import com.ideaspark.project.model.dto.response.TeamMemberListItemResponse;
import com.ideaspark.project.model.dto.response.TeamMemberRoleUpdateResponse;
import com.ideaspark.project.model.dto.response.TeamMemberRemoveResponse;
import com.ideaspark.project.model.dto.response.TeamExitResponse;
import com.ideaspark.project.model.dto.response.TeamTransferOwnershipResponse;
import com.ideaspark.project.model.dto.response.TeamUpdateResponse;
import com.ideaspark.project.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 团队管理控制器
 * 提供团队创建、成员管理、权限管理等接口
 */
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "团队管理", description = "团队创建、成员管理、权限管理等接口")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/my")
    @Operation(summary = "获取我的团队列表", description = "分页获取当前用户加入的所有团队")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<?> getMyTeams(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "团队查询参数")
            @ModelAttribute TeamMyListRequest request) {
        Page<TeamListItemResponse> page = teamService.getMyTeams(userId, request);
        int currentPage = request != null && request.getPage() != null && request.getPage() >= 1
                ? request.getPage()
                : 1;
        Map<String, Object> data = Map.of(
                "teams", page.getContent(),
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

    @GetMapping("/{uuid}")
    @Operation(summary = "获取团队详情", description = "根据团队UUID获取团队详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "404", description = "团队不存在")
    })
    public ResponseEntity<?> getTeamDetail(
            @Parameter(description = "团队UUID", required = true)
            @PathVariable("uuid") String teamUuid,
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId) {
        TeamDetailResponse detail = teamService.getTeamDetail(teamUuid, userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", detail
        ));
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "更新团队信息", description = "更新团队基本信息（需要团队管理员或所有者权限）")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ResponseEntity<?> updateTeam(
            @Parameter(description = "团队UUID", required = true)
            @PathVariable("uuid") String teamUuid,
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "团队更新参数", required = true)
            @RequestBody TeamUpdateRequest request) {
        TeamUpdateResponse result = teamService.updateTeam(teamUuid, userId, request);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "团队信息更新成功",
                "data", result
        ));
    }

    @PostMapping("/collaboration")
    @Operation(summary = "创建协作团队", description = "创建新的协作团队，创建者自动成为团队所有者")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数错误或团队名称已存在"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    public ResponseEntity<?> createCollaborationTeam(
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "团队创建参数", required = true)
            @RequestBody TeamCreateCollaborationRequest request) {
        TeamCreateCollaborationResponse result = teamService.createCollaborationTeam(userId, request);
        return ResponseEntity.status(201).body(Map.of(
                "status", 201,
                "message", "协作团队创建成功",
                "data", result
        ));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> dissolveTeam(@PathVariable("uuid") String teamUuid,
                                          @RequestAttribute("userId") Long userId,
                                          @RequestBody(required = false) TeamDissolveRequest request) {
        TeamDissolveResponse result = teamService.dissolveTeam(teamUuid, userId, request);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "团队解散成功",
                "data", result
        ));
    }

    @GetMapping("/{uuid}/members")
    public ResponseEntity<?> getTeamMembers(@PathVariable("uuid") String teamUuid,
                                            @RequestAttribute("userId") Long userId,
                                            @ModelAttribute TeamMemberListRequest request) {
        Page<TeamMemberListItemResponse> page = teamService.getTeamMembers(teamUuid, userId, request);
        int currentPage = request != null && request.getPage() != null && request.getPage() >= 1
                ? request.getPage()
                : 1;
        Map<String, Object> data = Map.of(
                "members", page.getContent(),
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

    @PutMapping("/{uuid}/members/{memberId}/role")
    public ResponseEntity<?> updateMemberRole(@PathVariable("uuid") String teamUuid,
                                              @PathVariable("memberId") String memberId,
                                              @RequestAttribute("userId") Long userId,
                                              @RequestBody TeamMemberRoleUpdateRequest request) {
        TeamMemberRoleUpdateResponse result = teamService.updateMemberRole(teamUuid, memberId, userId, request);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "成员角色修改成功",
                "data", result
        ));
    }

    @DeleteMapping("/{uuid}/members/{memberId}")
    public ResponseEntity<?> removeMember(@PathVariable("uuid") String teamUuid,
                                          @PathVariable("memberId") String memberId,
                                          @RequestAttribute("userId") Long userId) {
        TeamMemberRemoveResponse result = teamService.removeMember(teamUuid, memberId, userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "成员移除成功",
                "data", result
        ));
    }

    @DeleteMapping("/{uuid}/members/self")
    public ResponseEntity<?> exitTeam(@PathVariable("uuid") String teamUuid,
                                      @RequestAttribute("userId") Long userId) {
        TeamExitResponse result = teamService.exitTeam(teamUuid, userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "成功退出团队",
                "data", result
        ));
    }

    @PostMapping("/{uuid}/transfer-ownership")
    public ResponseEntity<?> transferOwnership(@PathVariable("uuid") String teamUuid,
                                               @RequestAttribute("userId") Long userId,
                                               @RequestBody TeamTransferOwnershipRequest request) {
        TeamTransferOwnershipResponse result = teamService.transferOwnership(teamUuid, userId, request);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "团队所有权转让成功",
                "data", result
        ));
    }

    @PostMapping("/{uuid}/invitations")
    public ResponseEntity<?> sendInvitations(@PathVariable("uuid") String teamUuid,
                                             @RequestAttribute("userId") Long userId,
                                             @RequestBody TeamInvitationSendRequest request) {
        TeamInvitationSendResponse result = teamService.sendInvitations(teamUuid, userId, request);
        return ResponseEntity.status(201).body(Map.of(
                "status", 201,
                "message", "邀请发送成功",
                "data", result
        ));
    }

    @GetMapping("/{uuid}/projects")
    @Operation(summary = "获取团队项目列表", description = "获取指定团队的所有项目列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "无权限访问"),
        @ApiResponse(responseCode = "404", description = "团队不存在")
    })
    public ResponseEntity<?> getTeamProjects(
            @Parameter(description = "团队UUID", required = true)
            @PathVariable("uuid") String teamUuid,
            @Parameter(description = "用户ID（从token中获取）", hidden = true)
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "页码，默认1")
            @ModelAttribute TeamMyListRequest request) {
        int page = request != null && request.getPage() != null && request.getPage() >= 1
                ? request.getPage()
                : 1;
        int size = request != null && request.getSize() != null && request.getSize() > 0
                ? request.getSize()
                : 20;
        var pageResult = teamService.getTeamProjects(teamUuid, userId, page, size);
        Map<String, Object> data = Map.of(
                "projects", pageResult.getContent(),
                "total", pageResult.getTotalElements(),
                "page", page,
                "size", pageResult.getSize()
        );
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", data
        ));
    }
}
