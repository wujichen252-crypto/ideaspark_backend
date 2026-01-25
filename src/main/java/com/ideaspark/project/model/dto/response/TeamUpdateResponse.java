package com.ideaspark.project.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamUpdateResponse {

    private String uuid;

    private String name;

    private String avatarUrl;

    private String description;

    private LocalDateTime updatedAt;
}
