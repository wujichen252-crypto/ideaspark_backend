package com.ideaspark.project.model.dto.request;

import lombok.Data;

@Data
public class UserCreateRequest {

    private String account;
    private String username;
    private String phone;
}

