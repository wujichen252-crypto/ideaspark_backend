package com.ideaspark.project.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectMyListItemResponse {

    private String id;

    private String name;

    private String description;

    private String category;

    private String coverUrl;

    private String status;

    private Integer progress;

    private String visibility;

    private Boolean allowFork;

    private Long ownerId;

    private String ownerName;

    private String teamId;

    private String teamName;

    private String myRole;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

