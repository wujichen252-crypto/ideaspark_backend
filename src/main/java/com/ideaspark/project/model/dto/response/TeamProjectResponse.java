package com.ideaspark.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 团队项目响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamProjectResponse {

    private String id;
    private String name;
    private String description;
    private String category;
    private String coverUrl;
    private String status;
    private String visibility;
    private Integer progress;
    private Long ownerId;
    private String ownerName;
    private String ownerAvatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
