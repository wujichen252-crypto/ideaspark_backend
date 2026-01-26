package com.ideaspark.project.model.dto.request;

import lombok.Data;

@Data
public class TeamInvitationInviteeRequest {

    private Long userId;

    private String email;

    private String role;
}
