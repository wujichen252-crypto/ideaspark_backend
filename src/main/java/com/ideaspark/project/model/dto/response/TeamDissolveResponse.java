package com.ideaspark.project.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamDissolveResponse {

    private String teamId;

    private String teamName;

    private LocalDateTime dissolvedAt;

    private String reason;
}
