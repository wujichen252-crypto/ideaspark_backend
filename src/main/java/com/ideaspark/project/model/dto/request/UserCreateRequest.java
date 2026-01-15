package com.ideaspark.project.model.dto.request;

import lombok.Data;

@Data
public class UserCreateRequest {

    private String username;
    private String email;
    private String password;
}

