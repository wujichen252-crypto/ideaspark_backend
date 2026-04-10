package com.ideaspark.project.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建项目响应
 */
@Data
public class CreateProjectResponse {

    /**
     * 项目ID
     */
    private String id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目分类
     */
    private String category;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 项目状态
     */
    private String status;

    /**
     * 项目进度
     */
    private Integer progress;

    /**
     * 可见性
     */
    private String visibility;

    /**
     * 是否允许Fork
     */
    private Boolean allowFork;

    /**
     * 项目标签
     */
    private List<String> tags;

    /**
     * 项目负责人ID
     */
    private Long ownerId;

    /**
     * 负责人名称
     */
    private String ownerName;

    /**
     * 所属团队ID
     */
    private String teamId;

    /**
     * 所属团队名称
     */
    private String teamName;

    /**
     * 当前用户角色
     */
    private String currentUserRole;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
