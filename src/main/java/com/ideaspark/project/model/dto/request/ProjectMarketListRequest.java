package com.ideaspark.project.model.dto.request;

import lombok.Data;

@Data
public class ProjectMarketListRequest {

    private String keyword;

    private String category;

    private Integer page;

    private Integer size;
}
