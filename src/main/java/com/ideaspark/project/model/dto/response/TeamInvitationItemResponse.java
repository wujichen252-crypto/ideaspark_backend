package com.ideaspark.project.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamInvitationItemResponse {

    private Long inviteeId;

    private String inviteeEmail;

    private String role;

    private String status;

    private String token;

    private LocalDateTime expiresAt;
}
