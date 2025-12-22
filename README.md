# ideaspark_backend

## 一、项目概述

这是一个基于 Spring Boot 3.4.1 的企业级项目脚手架，提供了完整的项目结构、常用组件集成和最佳实践，旨在帮助开发团队快速搭建稳定、可扩展的 Spring Boot 应用。

### 1.1 设计原则

- **按业务模块拆分，而非按技术层堆叠**
- **Controller 轻、Service 重、Repository 纯**
- 业务逻辑集中在 Service 层，禁止散落在 Controller 或 Entity 中
- 统一响应结构和全局异常处理
- 严格的模型分层：Entity、DTO、VO

### 1.2 核心功能

| 功能模块 | 技术实现 | 说明 |
|---------|---------|------|
| 安全认证 | Spring Security + JWT | 基于 OAuth2 资源服务器的 JWT 认证 |
| API文档 | Springfox Swagger3 | 自动生成 RESTful API 文档 |
| 数据访问 | Spring Data JPA + MySQL | 持久化框架和数据库支持 |
| 缓存 | Redis | 高性能缓存支持 |
| 消息队列 | RabbitMQ | 异步消息处理 |
| 文件处理 | Apache POI | Excel 导入导出 |
| PDF处理 | Apache PDFBox | PDF 生成、读取和修改 |
| Markdown | CommonMark | Markdown 转 HTML |
| 定时任务 | Spring Scheduling + Quartz | 灵活的定时任务支持 |
| 搜索功能 | Elasticsearch | 全文搜索支持 |
| 监控 | Spring Boot Actuator | 应用健康监控 |
| 配置中心 | Spring Cloud Config | 集中式配置管理 |
| 服务注册 | Spring Cloud Eureka | 服务注册与发现 |

## 二、快速开始

### 2.1 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.9+（可选）
- Elasticsearch 8.0+（可选）

### 2.2 安装与配置

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd springboot-scaffold
   ```

2. **配置数据库**
   - 创建数据库：`springboot_scaffold`
   - 修改 `application.yml` 中的数据库连接信息

3. **配置其他组件**（可选）
   - Redis：修改 `spring.redis` 配置
   - RabbitMQ：修改 `spring.rabbitmq` 配置
   - Elasticsearch：修改 `spring.elasticsearch` 配置

### 2.3 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/springboot-scaffold-1.0.0.jar
```

项目启动后，可通过以下地址访问：
- 应用首页：http://localhost:8080
- Swagger API文档：http://localhost:8080/swagger-ui.html
- Actuator监控：http://localhost:8080/actuator

## 三、项目结构

### 3.1 目录结构

```text
springboot-scaffold/
├── pom.xml                      # Maven 依赖配置
├── README.md                    # 项目文档
├── src/
│   └── main/
│       ├── java/com/example/project/
│       │   ├── ProjectApplication.java      # 应用启动类
│       │   ├── common/                      # 公共模块
│       │   │   ├── constants/               # 常量定义
│       │   │   ├── exception/               # 异常处理
│       │   │   ├── response/                # 统一响应
│       │   │   └── utils/                   # 工具类
│       │   ├── config/                      # 配置类
│       │   │   ├── JwtConfig.java           # JWT 认证配置
│       │   │   ├── SecurityConfig.java      # 安全配置
│       │   │   ├── SwaggerConfig.java       # Swagger 配置
│       │   │   ├── RedisConfig.java         # Redis 配置
│       │   │   ├── RabbitMQConfig.java      # RabbitMQ 配置
│       │   │   ├── ElasticsearchConfig.java # Elasticsearch 配置
│       │   │   ├── ScheduleConfig.java      # 定时任务配置
│       │   │   └── FileUploadConfig.java    # 文件上传配置
│       │   └── module/                      # 业务模块
│       │       └── user/                    # 用户模块示例
│       │           ├── controller/          # 控制器
│       │           ├── service/             # 业务逻辑
│       │           │   └── impl/            # 业务实现
│       │           ├── repository/          # 数据访问
│       │           └── model/               # 模型定义
│       │               ├── entity/          # 数据库实体
│       │               ├── dto/             # 数据传输对象
│       │               └── vo/              # 视图对象
│       └── resources/
│           ├── application.yml              # 应用配置
│           └── mapper/                      # MyBatis 映射文件（可选）
└── src/test/                                # 测试代码
```

### 3.2 模型分层

- **Entity**：与数据库表一一对应，不承载业务逻辑
- **DTO**：用于接口入参，可包含校验注解
- **VO**：用于接口出参，与前端字段保持稳定契约

## 四、核心组件使用

### 4.1 JWT 认证

#### 4.1.1 配置

在 `application.yml` 中配置 JWT 密钥：
```yaml
custom:
  jwt:
    secret: your-secret-key-change-me
    expiration: 3600000  # 1小时
    header: Authorization
```

#### 4.1.2 使用

1. **获取令牌**：实现登录接口，在登录成功后生成 JWT 令牌
2. **使用令牌**：在请求头中添加 `Authorization: Bearer <token>`
3. **权限控制**：使用 `@PreAuthorize` 注解进行权限控制

### 4.2 Excel 处理

#### 4.2.1 导出 Excel

```java
// 示例：导出用户列表
List<UserEntity> userList = userRepository.findAll();
ByteArrayOutputStream outputStream = ExcelUtil.exportExcel(userList, UserEntity.class, "用户列表");

// 设置响应头
response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
response.setHeader("Content-Disposition", "attachment; filename=用户列表.xlsx");
response.getOutputStream().write(outputStream.toByteArray());
```

#### 4.2.2 导入 Excel

```java
// 示例：导入用户列表
InputStream inputStream = file.getInputStream();
List<UserEntity> userList = ExcelUtil.importExcel(inputStream, UserEntity.class);
userRepository.saveAll(userList);
```

### 4.3 PDF 处理

#### 4.3.1 创建 PDF

```java
// 示例：创建简单 PDF
List<String> contentList = Arrays.asList("第一行内容", "第二行内容", "第三行内容");
ByteArrayOutputStream outputStream = PdfUtil.createPdf(contentList, "示例文档");

// 设置响应头
response.setContentType("application/pdf");
response.setHeader("Content-Disposition", "attachment; filename=示例文档.pdf");
response.getOutputStream().write(outputStream.toByteArray());
```

#### 4.3.2 读取 PDF

```java
// 示例：读取 PDF 内容
InputStream inputStream = new FileInputStream("示例文档.pdf");
String content = PdfUtil.readPdfContent(inputStream);
```

### 4.4 Markdown 处理

```java
// 示例：Markdown 转 HTML
String markdown = "# 标题\n\n这是一段**加粗**的文本。";
String html = MarkdownUtil.markdownToHtml(markdown);
```

### 4.5 定时任务

#### 4.5.1 使用 Spring Scheduling

```java
@Component
public class ScheduleTask {
    
    @Scheduled(cron = "0 0 0 * * ?")  // 每天凌晨执行
    public void dailyTask() {
        // 执行定时任务逻辑
    }
    
    @Scheduled(fixedRate = 5000)  // 每5秒执行一次
    public void fixedRateTask() {
        // 执行定时任务逻辑
    }
}
```

#### 4.5.2 使用 Quartz

```java
@Configuration
public class QuartzConfig {
    
    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(MyQuartzJob.class)
                .withIdentity("myJob")
                .storeDurably()
                .build();
    }
    
    @Bean
    public Trigger trigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("myTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                .build();
    }
}
```

## 五、开发指南

### 5.1 新增业务模块

1. **创建模块目录**：在 `module` 下创建新的业务模块目录（如 `order`）
2. **创建分层结构**：
   - controller：控制器层
   - service/impl：业务逻辑层
   - repository：数据访问层
   - model/entity：数据库实体
   - model/dto：请求参数
   - model/vo：响应数据

3. **实现业务逻辑**：
   - Controller：处理请求，调用 Service
   - Service：实现业务逻辑，使用 Repository 访问数据
   - Repository：定义数据访问方法

### 5.2 统一响应

所有 API 接口都应使用 `ApiResponse` 统一响应格式：

```java
@PostMapping
public ApiResponse<UserVO> create(@RequestBody CreateUserDTO dto) {
    UserVO userVO = userService.createUser(dto);
    return ApiResponse.success(userVO);
}
```

### 5.3 异常处理

- 业务异常：抛出 `BusinessException`
- 系统异常：由全局异常处理器处理

```java
if (userRepository.existsByUsername(dto.getUsername())) {
    throw new BusinessException("用户名已存在");
}
```

### 5.4 事务管理

在 Service 层方法上使用 `@Transactional` 注解：

```java
@Transactional
@Override
public UserVO createUser(CreateUserDTO dto) {
    // 业务逻辑
}
```

## 六、配置说明

### 6.1 核心配置文件

- `application.yml`：主配置文件
- 支持多环境配置：`application-dev.yml`、`application-test.yml`、`application-prod.yml`

### 6.2 主要配置项

#### 6.2.1 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/springboot_scaffold?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
```

#### 6.2.2 Redis 配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

#### 6.2.3 JWT 配置

```yaml
custom:
  jwt:
    secret: your-secret-key-change-me
    expiration: 3600000
    header: Authorization
```

## 七、部署指南

### 7.1 打包方式

```bash
# 打包成可执行 JAR
mvn clean package
```

### 7.2 运行方式

```bash
# 直接运行
java -jar target/springboot-scaffold-1.0.0.jar

# 指定环境运行
java -jar -Dspring.profiles.active=prod target/springboot-scaffold-1.0.0.jar
```

### 7.3 Docker 部署

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
ADD target/springboot-scaffold-1.0.0.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

构建并运行：
```bash
docker build -t springboot-scaffold .
docker run -d -p 8080:8080 springboot-scaffold
```

## 八、代码规范

### 8.1 命名规范

- 包名：全小写，按业务语义命名
- 类名：PascalCase
- 方法/变量：camelCase
- 常量：全大写，下划线分隔

### 8.2 注释规范

- 类注释：使用 Javadoc 注释，说明类的功能和职责
- 方法注释：使用 Javadoc 注释，说明方法的功能、参数和返回值
- 代码注释：必要时添加注释，解释"为什么"，而非"是什么"

### 8.3 Git 提交规范

```text
feat(user): 新增用户注册接口
fix(order): 修复金额计算错误
docs: 更新 README 文档
style: 格式化代码
refactor: 重构用户服务
```

## 九、代码生成工具

### 9.1 概述

为了提高开发效率，脚手架提供了自动化代码生成工具，可以快速生成完整的业务模块代码结构。

### 9.2 使用方法

#### 9.2.1 直接运行

```bash
# 编译项目
mvn compile

# 运行代码生成器
mvn exec:java -Dexec.mainClass="com.example.project.generator.ModuleGenerator" -Dexec.args="模块名称 作者"
```

**示例**：
```bash
mvn exec:java -Dexec.mainClass="com.example.project.generator.ModuleGenerator" -Dexec.args="order admin"
```

#### 9.2.2 通过 IDE 运行

1. 打开 `ModuleGenerator` 类
2. 配置 `main` 方法的参数（模块名称、作者）
3. 运行 `main` 方法

### 9.3 生成的文件

代码生成工具会生成以下文件结构：

```text
module/模块名称/
├── controller/              # 控制器
├── service/                 # 业务逻辑
│   └── impl/               # 业务实现
├── repository/             # 数据访问
└── model/                  # 模型定义
    ├── entity/             # 数据库实体
    ├── dto/                # 数据传输对象
    └── vo/                 # 视图对象
```

### 9.4 自定义配置

可以通过修改 `ModuleGeneratorConfig` 类来自定义代码生成的配置：

- 基础包名
- 模块基础路径
- 是否生成特定文件
- 表前缀
- 默认作者

### 9.5 详细文档

完整的使用指南请参考：[业务模块代码生成器使用指南](docs/业务模块代码生成器使用指南.md)

## 十、常见问题

### 10.1 端口被占用

修改 `application.yml` 中的 `server.port` 配置：
```yaml
server:
  port: 8081
```

### 10.2 数据库连接失败

- 检查数据库服务是否启动
- 检查数据库连接信息是否正确
- 检查数据库用户权限

### 10.3 JWT 认证失败

- 检查 JWT 密钥是否一致
- 检查令牌是否过期
- 检查令牌格式是否正确

### 10.4 代码生成失败

- 检查模块名称是否符合规范（只能包含字母和数字）
- 检查是否有足够的文件写入权限
- 检查模板文件是否存在且完整

## 十一、版本历史

| 版本 | 日期 | 描述 |
|------|------|------|
| 1.0.0 | 2025-12-22 | 初始版本，包含核心功能 |

## 十二、许可证

MIT License

## 十三、联系方式

如有问题或建议，请联系项目维护者。
