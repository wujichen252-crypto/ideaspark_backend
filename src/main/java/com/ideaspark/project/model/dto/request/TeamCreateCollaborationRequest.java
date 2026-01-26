package com.ideaspark.project.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TeamCreateCollaborationRequest {

    private String name;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private String description;

}
