package com.ideaspark.project.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建项目请求
 */
@Data
public class CreateProjectRequest {

    /**
     * 项目名称（必填）
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称不能超过100个字符")
    private String name;

    /**
     * 所属团队ID（必填）
     */
    @NotBlank(message = "所属团队不能为空")
    private String teamId;

    /**
     * 可见性：private（默认）/ public
     */
    private String visibility = "private";

    /**
     * 项目描述
     */
    @Size(max = 2000, message = "项目描述不能超过2000个字符")
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
     * 项目类型：app / document
     */
    private String type;

    /**
     * 项目标签
     */
    private List<String> tags;

    /**
     * 是否允许Fork
     */
    private Boolean allowFork = true;
}
