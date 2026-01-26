package com.ideaspark.project.model.dto.response;

import lombok.Data;

@Data
public class TeamInvitationItemResponse {

    private Long inviteeId;

    private String inviteeEmail;

    private String role;

    private String status;
}
