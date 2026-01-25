package com.ideaspark.project.model.dto.request;

import lombok.Data;

@Data
public class TeamMemberListRequest {

    private Integer page;

    private Integer size;

    private String role;

    private String keyword;
}
