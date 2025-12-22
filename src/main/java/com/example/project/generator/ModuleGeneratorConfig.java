package com.example.project.generator;

import lombok.Data;

/**
 * 模块生成器配置
 * 用于配置代码生成的相关参数
 */
@Data
public class ModuleGeneratorConfig {

    /**
     * 基础包名
     */
    private String basePackage = "com.example.project";

    /**
     * 模块基础路径
     */
    private String moduleBasePath = "src/main/java/com/example/project/module";

    /**
     * 是否生成Controller
     */
    private boolean generateController = true;

    /**
     * 是否生成Service
     */
    private boolean generateService = true;

    /**
     * 是否生成Repository
     */
    private boolean generateRepository = true;

    /**
     * 是否生成Model
     */
    private boolean generateModel = true;

    /**
     * 表前缀
     */
    private String tablePrefix = "";

    /**
     * 作者
     */
    private String author = "admin";
}