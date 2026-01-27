package com.ideaspark.project.controller;

import com.ideaspark.project.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final TeamService teamService;

    @GetMapping("/validate")
    public ResponseEntity<?> validateInvitation(@RequestParam("token") String token,
                                                @RequestAttribute("userId") Long userId) {
        Map<String, Object> result = teamService.validateInvitationToken(token, userId);
        Object validObj = result.get("valid");
        boolean valid = validObj instanceof Boolean && (Boolean) validObj;
        if (valid) {
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "邀请验证成功",
                    "data", result
            ));
        }
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "message", "邀请链接已失效",
                "data", result
        ));
    }
}
