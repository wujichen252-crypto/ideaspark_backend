package com.ideaspark.project.model.dto.response;

import lombok.Data;

@Data
public class TeamMemberRemoveResponse {

    private Long memberId;

    private Long userId;

    private String userName;
}

