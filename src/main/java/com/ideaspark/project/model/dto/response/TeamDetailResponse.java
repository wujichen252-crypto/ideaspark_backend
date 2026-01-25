package com.ideaspark.project.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamDetailResponse {

    private String uuid;

    private String name;

    private String avatarUrl;

    private String description;

    private Boolean isPersonal;

    private String teamType;

    private Long ownerId;

    private String ownerName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String currentUserRole;

    private Long teamSize;

    private String projectId;
}
