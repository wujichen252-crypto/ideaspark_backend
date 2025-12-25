【项目技术栈】
- 后端语言：Java 17+
- 框架：Spring Boot 3.2.5 + Spring Data JPA
- ORM 框架：Spring Data JPA（基于 Hibernate）
- JSON 序列化：默认 Jackson
- 项目架构：按业务模块拆分的三层架构（Controller + Service 实现类 + Repository）

【项目结构规则】
- 项目按业务模块拆分：com.example.project.module.{moduleName}
- 每个模块内部按职责拆分：controller / service / repository / model
- 每个模块包含子包：controller（控制器）、service（业务逻辑，含impl子包）、repository（数据访问）、model（模型定义，含entity、dto、vo子包）
- 公共模块放在com.example.project.common包下，包含constants、exception、response、utils子包
- 配置类放在com.example.project.config包下

【分层职责强制约束】
- Controller：只负责接收参数、调用 Service、返回统一响应
  禁止：业务判断、事务、数据库操作
- Service：承载完整业务逻辑，允许事务控制（@Transactional）
- Repository：只负责数据库访问，禁止业务逻辑
- Entity：仅用于数据库映射，禁止作为接口返回对象

【数据模型规则】
- Entity：数据库实体，与数据库表一一对应，不承载业务逻辑
- DTO：数据传输对象，用于接口入参，可包含校验注解
- VO：视图对象，用于接口出参，与前端字段保持稳定契约
- 禁止 Entity ↔ Controller 直接暴露

【命名规范】
- 包名：全小写，单数，按业务语义
- 类名：PascalCase
- 方法 / 变量：lowerCamelCase
- 常量：全大写，下划线分隔
- Java 实体类字段名使用驼峰命名（如：userId, createTime），加上 @Column(name="xxx") 映射数据库字段
- 接口路径使用 RESTful 风格 + 动词，如 POST /api/user/register

【异常与响应规范】
- 所有接口统一使用 ResponseEntity<?> 包装 JSON 格式响应
- 接口返回值格式：ResponseEntity.ok(Map.of("status", 200, "message", "xxx成功", "data", xxx))
- 失败返回格式：ResponseEntity.badRequest().body(Map.of("status", 400, "message", "xxx失败：" + e.getMessage()))
- 所有业务异常、参数错误统一抛出 BusinessException
- Controller 层统一捕获 BusinessException，使用 @ControllerAdvice 进行全局异常处理
- 不再使用原生 RuntimeException 或 IllegalArgumentException

【代码风格要求】
- 优先清晰命名，注释用于解释"为什么"
- 禁止无意义缩写、拼音命名
- 禁止在 Controller / Entity 中编写业务逻辑
- DTO 映射不使用 BeanUtils.copyProperties，采用手动赋值
- 所有 Entity → DTO/VO 的转换都采用手动赋值，建议封装在 Service 层的私有方法中统一处理

【开发约束】
- 分页查询使用 Pageable 和 Page<T>
- 请求参数统一封装为 DTO 请求类（如：UserRequest），命名为 XXXRequest
- 响应对象使用 VO（视图对象），命名为 XXXVO，放在 vo 包中
- Service 类不定义接口，直接使用 @Service 的实现类
- 复杂业务逻辑实现放在 impl 子包中
- 控制器类使用 @RestController + @RequestMapping("/api/{moduleName}")
- 所有新增代码必须符合以上规则，否则视为不合规
