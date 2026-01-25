package com.ideaspark.project.model.dto.request;

import lombok.Data;

@Data
public class TeamUpdateRequest {

    private String name;

    private String avatarUrl;

    private String description;
}
