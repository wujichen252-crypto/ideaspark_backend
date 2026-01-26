package com.ideaspark.project.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TeamInvitationSendResponse {

    private int totalInvited;

    private int successCount;

    private List<TeamInvitationItemResponse> invitations;
}
