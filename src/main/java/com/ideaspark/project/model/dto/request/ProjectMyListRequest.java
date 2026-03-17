package com.ideaspark.project.model.dto.request;

import lombok.Data;

@Data
public class ProjectMyListRequest {

    private Integer page;

    private Integer size;

    private String keyword;

    private String status;
}

