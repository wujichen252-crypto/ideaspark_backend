package com.ideaspark.project.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectMarketDetailResponse {

    private String id;

    private String name;

    private String description;

    private String detailedDescription;

    private String category;

    private String coverUrl;

    private String type;

    private String currentModule;

    private String status;

    private Integer progress;

    private String visibility;

    private Boolean allowFork;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String parentId;

    private Long ownerId;

    private String ownerName;

    private String ownerAvatar;

    private String teamId;

    private String teamName;

    private String teamAvatar;

    private Boolean teamIsPersonal;

    private Integer teamSize;

    private Long likeCount;

    private List<String> tags;
}
