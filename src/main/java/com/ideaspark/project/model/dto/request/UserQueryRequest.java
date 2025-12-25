package com.ideaspark.project.model.dto.request;

import lombok.Data;

@Data
public class UserQueryRequest {

    private Integer page;
    private Integer size;
    private String name;
}

