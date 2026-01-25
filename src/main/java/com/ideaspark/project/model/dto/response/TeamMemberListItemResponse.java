package com.ideaspark.project.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamMemberListItemResponse {

    private String id;

    private Long userId;

    private String userName;

    private String userAvatar;

    private String role;

    private String roleCn;

    private LocalDateTime joinedAt;

    private boolean canRemove;

    private boolean canChangeRole;
}
