package com.ideaspark.project.model.dto.request;

import lombok.Data;

@Data
public class TeamDissolveRequest {

    private String confirmation;

    private String reason;
}
