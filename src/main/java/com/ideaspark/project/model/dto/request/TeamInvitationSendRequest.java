package com.ideaspark.project.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class TeamInvitationSendRequest {

    private List<TeamInvitationInviteeRequest> invitees;
}
