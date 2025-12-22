【项目技术栈】
- 后端语言：Java
- 框架：Spring Boot
- 架构目标：中小型可维护项目，强调工程规范与分层设计

【项目结构规则】
- 项目必须按“业务模块”拆分，统一放在 module 目录下（如 user、order）
- 每个模块内部按职责拆分：controller / service / repository / model
- 禁止按技术层全局堆叠（如全局 controller、service 目录）

【分层职责强制约束】
- Controller：只负责接收参数、调用 Service、返回统一响应
  禁止：业务判断、事务、数据库操作
- Service：承载完整业务逻辑，允许事务控制（@Transactional）
- Repository：只负责数据库访问，禁止业务逻辑
- Entity：仅用于数据库映射，禁止作为接口返回对象

【数据模型规则】
- Entity：数据库实体
- DTO：接口入参对象（可含参数校验）
- VO：接口返回对象
- 禁止 Entity ↔ Controller 直接暴露

【命名规范】
- 包名：全小写，单数，按业务语义
- 类名：PascalCase
- 方法 / 变量：lowerCamelCase
- 常量：全大写，下划线分隔

【异常与响应规范】
- 所有接口统一返回 ApiResponse
- 禁止在 Controller 中 try-catch 业务异常
- 必须使用全局异常处理（@RestControllerAdvice）

【代码风格要求】
- 优先清晰命名，注释用于解释“为什么”
- 禁止无意义缩写、拼音命名
- 禁止在 Controller / Entity 中编写业务逻辑

【开发约束】
- 不区分 dev / prod 环境，统一 application.yml
- 不引入未说明的第三方框架
- 所有新增代码必须符合以上规则，否则视为不合规
