一、总体约定（强制）
项目	规则
架构	Controller → Service → Repository
ORM	Spring Data JPA
Service	不定义接口，仅实现类
DTO 映射	禁止 BeanUtils.copyProperties
返回格式	ResponseEntity<Map<String,Object>>
分页	Pageable + Page<T>
二、包结构（固定）
禁止新增多余分层。
三、命名规范
1. Entity
Java：驼峰
DB：下划线
必须显式映射
@Column(name = "create_time")
private LocalDateTime createTime;
2. 接口路径
REST 风格
明确动词
POST /api/user/create
GET  /api/user/list
POST /api/user/delete
四、Controller 层规范
Controller 只允许：
接收 DTO
调用 Service
封装返回
成功返回
return ResponseEntity.ok(Map.of(
    "status", 200,
    "message", "成功",
    "data", data
));
Controller 中禁止：
业务判断
try-catch
数据转换
五、Service 层规范
所有业务逻辑、校验、事务在 Service
所有错误 只抛 BusinessException
不返回 Map / ResponseEntity
六、DTO 规范
Request
public class UserRequest { }
命名：XXXRequest
仅用于接收参数
Response
public class UserResponse { }
命名：XXXResponse
禁止直接返回 Entity
七、Entity → DTO 映射规范（强制）
必须手动 set
放在 Service 私有方法中
禁止 BeanUtils / MapStruct
八、异常规范（统一 BusinessException）