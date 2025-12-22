# Spring Boot 项目代码风格与工程规范文档

> 适用范围：中小型 Spring Boot Web 项目 / 多人协作项目 / 学习向但具备工程实践价值的项目

---

## 一、规范目标与设计原则

### 1.1 规范目标

* 保证项目 **结构清晰、职责明确、可长期维护**
* 降低多人协作时的理解成本与沟通成本
* 约束代码风格，避免“能跑就行”的随意实现
* 服务于 **真实项目**，而非只为教学示例

### 1.2 设计原则

* **按业务模块拆分，而非按技术层堆叠**
* **Controller 轻、Service 重、Repository 纯**
* 业务逻辑集中，禁止散落在 Controller / Entity 中
* 命名优先于注释，注释用于解释“为什么”

---

## 二、项目整体结构规范

### 2.1 推荐目录结构

```text
project-name/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/
│       │   └── com.example.project/
│       │       ├── ProjectApplication.java
│       │       │
│       │       ├── module/                # 业务模块（核心）
│       │       │   ├── user/
│       │       │   │   ├── controller/    # 接口层
│       │       │   │   ├── service/       # 业务接口
│       │       │   │   │   └── impl/      # 业务实现
│       │       │   │   ├── repository/    # 数据访问层
│       │       │   │   ├── model/
│       │       │   │   │   ├── entity/    # 数据库实体
│       │       │   │   │   ├── dto/       # 接口入参
│       │       │   │   │   └── vo/        # 接口出参
│       │       │   │   └── UserConstants.java
│       │       │   │
│       │       │   └── order/
│       │       │       └── ...（结构同 user）
│       │       │
│       │       ├── common/                # 全局公共模块
│       │       │   ├── exception/
│       │       │   ├── response/
│       │       │   ├── utils/
│       │       │   └── constants/
│       │       │
│       │       └── config/                # Spring 配置类
│       │           ├── WebConfig.java
│       │           └── JacksonConfig.java
│       │
│       └── resources/
│           ├── application.yml
│           └── mapper/                    # MyBatis XML（如使用）
│
└── src/test/java/
    └── com.example.project/
        └── module/
            └── user/
                └── UserServiceTest.java
```

---

## 三、分层职责规范（核心）

### 3.1 Controller 层（接口层）

**职责：**

* 接收请求参数（DTO）
* 调用 Service 层
* 返回统一响应结构

**禁止行为：**

* 编写复杂业务逻辑
* 直接操作数据库
* 编写事务控制逻辑

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ApiResponse<UserVO> create(@RequestBody CreateUserDTO dto) {
        return ApiResponse.success(userService.createUser(dto));
    }
}
```

---

### 3.2 Service 层（业务层）

**职责：**

* 承载完整业务流程
* 编排多个 Repository 或外部服务
* 定义事务边界（`@Transactional`）

```java
public interface UserService {
    UserVO createUser(CreateUserDTO dto);
}
```

```java
@Service
public class UserServiceImpl implements UserService {

    @Transactional
    @Override
    public UserVO createUser(CreateUserDTO dto) {
        // 1. 业务校验
        // 2. 数据持久化
        // 3. 结果组装
        return null;
    }
}
```

---

### 3.3 Repository 层（数据访问层）

**职责：**

* 只负责数据库访问
* 不允许出现业务判断

```java
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsername(String username);
}
```

---

## 四、模型分层规范

### 4.1 Entity（实体）

* 与数据库表一一对应
* 不承载业务逻辑
* 禁止作为接口返回对象

```java
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
}
```

### 4.2 DTO（数据传输对象）

* 用于接口入参
* 可承载校验注解

```java
public class CreateUserDTO {
    @NotBlank
    private String username;
}
```

### 4.3 VO（返回对象）

* 用于接口出参
* 与前端字段保持稳定契约

```java
public class UserVO {
    private Long id;
    private String username;
}
```

---

## 五、命名规范

### 5.1 包名

* 全小写
* 按业务语义命名

```text
user
order
common
```

### 5.2 类与方法

* 类名：PascalCase
* 方法 / 变量：lowerCamelCase

```java
createUser()
getOrderList()
```

### 5.3 常量

* 全大写，下划线分隔

```java
public static final int MAX_RETRY_COUNT = 3;
```

---

## 六、统一响应与异常规范

### 6.1 统一响应结构

```java
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
}
```

```java
ApiResponse.success(data);
ApiResponse.error(400, "参数错误");
```

---

### 6.2 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }
}
```

---

## 七、注释规范

### 7.1 类注释

```java
/**
 * 用户服务
 * 负责用户注册、信息维护等核心业务
 */
```

### 7.2 方法注释

```java
/**
 * 创建用户
 *
 * @param dto 用户创建参数
 * @return 用户信息
 */
```

---

## 八、版本控制与协作规范

* 禁止直接提交 main 分支
* 功能开发：`feature/xxx`
* Bug 修复：`fix/xxx`

### 提交信息格式

```text
feat(user): 新增用户注册接口
fix(order): 修复金额计算错误
```

---

## 九、规范总结

* 本规范以 **工程可维护性** 为第一目标
* 鼓励一致性，减少个人风格差异
* 当规范与效率冲突时，以团队讨论结果为准

> **规范不是束缚，而是为了让项目活得更久。**
