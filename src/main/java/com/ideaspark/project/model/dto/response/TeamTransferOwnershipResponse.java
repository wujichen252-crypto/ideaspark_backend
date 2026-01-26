package com.ideaspark.project.model.dto.response;

import lombok.Data;

@Data
public class TeamTransferOwnershipResponse {

    private String teamId;

    private Long oldOwnerId;

    private Long newOwnerId;
}
