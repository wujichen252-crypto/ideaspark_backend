package com.ideaspark.project.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProjectMarketListItemResponse {

    private String projectImage;

    private String projectName;

    private String ownerName;

    private String ownerAvatar;

    private Long likeCount;

    private List<String> tags;
}
