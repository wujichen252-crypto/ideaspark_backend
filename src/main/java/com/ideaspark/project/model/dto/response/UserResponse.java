package com.ideaspark.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Integer id;
    private String account;
    private String username;
    private String phone;
    private LocalDateTime createTime;
    private String role;
    private String state;
}

