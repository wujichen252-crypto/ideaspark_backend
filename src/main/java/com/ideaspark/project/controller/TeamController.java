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

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyTeams(@RequestAttribute("userId") Long userId,
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
    public ResponseEntity<?> getTeamDetail(@PathVariable("uuid") String teamUuid,
                                           @RequestAttribute("userId") Long userId) {
        TeamDetailResponse detail = teamService.getTeamDetail(teamUuid, userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取成功",
                "data", detail
        ));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateTeam(@PathVariable("uuid") String teamUuid,
                                        @RequestAttribute("userId") Long userId,
                                        @RequestBody TeamUpdateRequest request) {
        TeamUpdateResponse result = teamService.updateTeam(teamUuid, userId, request);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "团队信息更新成功",
                "data", result
        ));
    }

    @PostMapping("/collaboration")
    public ResponseEntity<?> createCollaborationTeam(@RequestAttribute("userId") Long userId,
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
}
