package com.ideaspark.project.model.dto.response;

import lombok.Data;

@Data
public class TeamMemberRoleUpdateResponse {

    private Long memberId;

    private Long userId;

    private String userName;

    private String oldRole;

    private String newRole;
}
