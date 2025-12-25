package com.ideaspark.project.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UserDeleteRequest {

    private List<Integer> userIds;
}

