package com.example.project.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * 业务模块代码生成器
 * 用于自动生成业务模块的完整代码结构
 */
public class ModuleGenerator {

    private final Configuration configuration;
    private final ModuleGeneratorConfig config;

    /**
     * 构造函数
     *
     * @param config 生成器配置
     */
    public ModuleGenerator(ModuleGeneratorConfig config) {
        this.config = config;
        this.configuration = new Configuration(Configuration.VERSION_2_3_32);
        try {
            // 设置模板目录
            configuration.setClassForTemplateLoading(this.getClass(), "/templates");
            configuration.setDefaultEncoding("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("初始化模板引擎失败", e);
        }
    }

    /**
     * 生成业务模块
     *
     * @param moduleName 模块名称（小写，如：user）
     * @param author     作者
     */
    public void generateModule(String moduleName, String author) {
        if (StringUtils.isBlank(moduleName)) {
            throw new IllegalArgumentException("模块名称不能为空");
        }

        // 格式化名称
        String moduleNameLower = moduleName.toLowerCase();
        String moduleNameUpper = StringUtils.capitalize(moduleNameLower);
        String tableName = moduleNameLower + "s"; // 表名默认是模块名复数

        // 准备数据模型
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("moduleName", moduleNameLower);
        dataModel.put("moduleNameUpper", moduleNameUpper);
        dataModel.put("tableName", tableName);
        dataModel.put("author", author);
        dataModel.put("package", config.getBasePackage() + ".module." + moduleNameLower);
        dataModel.put("basePackage", config.getBasePackage());

        try {
            // 创建模块目录结构
            createDirectoryStructure(moduleNameLower);

            // 生成各个文件
            generateFile("entity.ftl", dataModel, getFilePath(moduleNameLower, "model/entity", moduleNameUpper + "Entity.java"));
            generateFile("dto.ftl", dataModel, getFilePath(moduleNameLower, "model/dto", "Create" + moduleNameUpper + "DTO.java"));
            generateFile("vo.ftl", dataModel, getFilePath(moduleNameLower, "model/vo", moduleNameUpper + "VO.java"));
            generateFile("repository.ftl", dataModel, getFilePath(moduleNameLower, "repository", moduleNameUpper + "Repository.java"));
            generateFile("service.ftl", dataModel, getFilePath(moduleNameLower, "service", moduleNameUpper + "Service.java"));
            generateFile("serviceImpl.ftl", dataModel, getFilePath(moduleNameLower, "service/impl", moduleNameUpper + "ServiceImpl.java"));
            generateFile("controller.ftl", dataModel, getFilePath(moduleNameLower, "controller", moduleNameUpper + "Controller.java"));

            System.out.println("\n业务模块 '" + moduleNameUpper + "' 生成成功！");
            System.out.println("生成路径：" + config.getModuleBasePath());

        } catch (Exception e) {
            throw new RuntimeException("生成模块失败", e);
        }
    }

    /**
     * 创建模块目录结构
     *
     * @param moduleName 模块名称
     */
    private void createDirectoryStructure(String moduleName) {
        String modulePath = config.getModuleBasePath() + File.separator + moduleName;
        
        // 创建目录列表
        String[] directories = {
            modulePath + File.separator + "controller",
            modulePath + File.separator + "service",
            modulePath + File.separator + "service/impl",
            modulePath + File.separator + "repository",
            modulePath + File.separator + "model/entity",
            modulePath + File.separator + "model/dto",
            modulePath + File.separator + "model/vo"
        };

        // 创建目录
        for (String dir : directories) {
            File file = new File(dir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    throw new RuntimeException("创建目录失败: " + dir);
                }
            }
        }
    }

    /**
     * 生成文件
     *
     * @param templateName 模板名称
     * @param dataModel    数据模型
     * @param filePath     文件路径
     */
    private void generateFile(String templateName, Map<String, Object> dataModel, String filePath) throws Exception {
        Template template = configuration.getTemplate(templateName);
        File file = new File(filePath);
        
        // 如果文件已存在，跳过
        if (file.exists()) {
            System.out.println("文件已存在，跳过: " + filePath);
            return;
        }
        
        // 创建文件
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        
        try (Writer out = new FileWriter(file)) {
            template.process(dataModel, out);
            System.out.println("生成文件: " + filePath);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("生成文件失败: " + filePath, e);
        }
    }

    /**
     * 获取文件路径
     *
     * @param moduleName 模块名称
     * @param subPath    子路径
     * @param fileName   文件名
     * @return 文件路径
     */
    private String getFilePath(String moduleName, String subPath, String fileName) {
        return config.getModuleBasePath() + File.separator + moduleName + File.separator + subPath + File.separator + fileName;
    }

    /**
     * 主方法，用于测试
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        // 配置生成器
        ModuleGeneratorConfig config = new ModuleGeneratorConfig();
        config.setBasePackage("com.example.project");
        config.setModuleBasePath("src/main/java/com/example/project/module");

        // 创建生成器
        ModuleGenerator generator = new ModuleGenerator(config);

        // 生成模块
        String moduleName = args.length > 0 ? args[0] : "order";
        String author = args.length > 1 ? args[1] : "admin";
        generator.generateModule(moduleName, author);
    }
}