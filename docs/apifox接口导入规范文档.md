## 💎 基于自定义接口的 Apifox 规范调整



核心思想：**将资源标识符（ID）从 Path 转移到 Query 或 Body 中**，并根据 HTTP 方法的语义来确定最佳位置。



### 1. 🔍 GET 方法（查询/获取资源详情/列表）



| **规范项目**    | **Apifox 实践建议**                                          | **测试便利性考量**                                           |
| --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **Path 设计**   | 使用集合路径，不含 ID。例如：`/api/v1/users/get_detail` 或 `/api/v1/user_details/` | URL 简洁，不暴露 ID。                                        |
| **ID 参数位置** | **Query** 参数。例如：`?user_id=123`。                       | **最方便**：GET 请求的标准做法。Apifox 中可直接在 Query 栏目编辑 ID。 |
| **批量查询**    | 仍使用 Query，例如：`?user_ids=1,2,3`。                      | 方便测试人员一次性输入多个 ID 进行批量测试。                 |
| **成功响应**    | **必须** 在 **成功用例 (200)** 中，展示正确的 ID 返回值。    |                                                              |



### 2. 📝 POST 方法（创建资源）



**与标准 RESTful 保持一致，无需特殊调整。**

| **规范项目**    | **Apifox 实践建议**                                          | **测试便利性考量**                                           |
| --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **Path 设计**   | 集合路径。例如：`/api/v1/users/` 或 `/api/v1/user_create/`。 |                                                              |
| **ID 参数位置** | **Body - application/json**。ID 是由后端生成，不应出现在请求中（除非是客户端指定的业务 ID）。 | 如果需要传递父资源的 ID（例如 `创建订单` 需要 `user_id`），将其作为 JSON Body 的一个字段传递。 |



### 3. 🔄 PUT/PATCH 方法（修改/更新资源）



这是调整的关键点，资源标识符 ID 需要从 Path 转移到 Body 中。

| **规范项目**    | **Apifox 实践建议**                                          | **测试便利性考量**                                           |
| --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **Path 设计**   | 使用集合路径。例如：`/api/v1/user_update/` 或 `/api/v1/users/update_profile`。 |                                                              |
| **ID 参数位置** | **Body - application/json**。将 ID 作为 JSON Body 的**第一个**必传字段（例如：`{"user_id": 123, "name": "new name"}`）。 | **清晰明确**：ID 与要更新的数据放在一起，更像一个完整的“更新对象”。 |
| **批量更新**    | Body 中包含一个 ID 列表和要更新的字段。例如：`{"user_ids": [1, 2, 3], "status": "active"}`。 | 非常适合 Django/DRF 的批量操作视图，方便测试人员设计批量用例。 |
| **字段规范**    | **必须** 在 Apifox 的 Body 参数定义中，将 `user_id` 字段的**描述**清晰标记为：**“待更新资源的唯一标识符”**。 |                                                              |



### 4. 🗑️ DELETE 方法（删除资源）



与 GET 类似，ID 放在 Path 不方便时，应放在 Query 或 Body 中。

| **规范项目**    | **Apifox 实践建议**                                          | **测试便利性考量**                                           |
| --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **Path 设计**   | 集合路径。例如：`/api/v1/users/delete/`。                    |                                                              |
| **ID 参数位置** | **Body - application/json**，传递一个 ID 列表。例如：`{"user_ids": [123, 456]}`。 | **最推荐**：删除操作通常需要支持**批量删除**，JSON Body 最灵活。 |
| **单个删除**    | 也可以使用 JSON Body，只包含一个 ID。                        | 统一了单删和批量删除的接口结构，方便前后端和测试人员理解。   |
| **成功响应**    | 推荐返回 `200 OK`，并在 Body 中返回受影响的行数，而非 `204 No Content`（更便于测试断言）。 |                                                              |



### ✅ 总结对 Apifox 测试的优势



即使您不将 ID 放在路由中，遵循上述规范，对测试人员仍有巨大便利：

1. **参数位置一致性**：GET 用 Query，POST/PUT/DELETE 用 Body，易于遵循。
2. **批量操作便利**：通过将 ID 设计为 Body 中的数组，测试人员可以轻松设计**批量测试用例**。
3. **调试用例清晰**：在 Apifox 的 **调试用例** 中，无论是 Query 还是 Body 参数，都会被清晰地保存和展示。
4. **模型引用**：对于 Body 中的 ID 和数据，仍可引用您定义的 **数据模型**，确保字段描述和类型正确。

---

## 📊 示例与规范补充：报名审核统计接口（GET）

为满足“复杂的筛选（业务逻辑）应独立为特定接口”的规范要求，本项目新增统计查询接口，供用户查看自身报名审核情况，并按组别（primary/middle）分类统计。

### 1. 接口定义（Apifox 导入）

- 路径：`/api/sign/applications/stats/`
- 方法：`GET`
- 标签：`Sign`
- 认证：`Bearer JWT`
- 参数位置：`Query`
- 设计依据：遵循本规范的 GET 规则（集合路径、不含 ID；参数通过 Query 传递）。

### 2. Query 参数说明

- `start_date`：字符串（`yyyy-MM-dd`），可选；统计起始日期。
- `end_date`：字符串（`yyyy-MM-dd`），可选；统计结束日期。
- `groups`：数组（取值：`primary`、`middle`），可选；统计的组别列表，默认统计两组。
  - Apifox 建议：`style=form`, `explode=true`，在 Query 栏以多个同名参数输入，如：`groups=primary&groups=middle`。
- `include_overall`：布尔，默认 `true`；是否包含总体统计（overall）。

### 3. 响应模型（组件引用）

- `SignApplicationStatsSummary`：统计摘要模型，包含：
  - `total`（报名总数）、`rejected`（已驳回）、`pending`（待审核）、`approved`（已通过）。
- `SignApplicationStatsResponse`：整体响应模型，包含：
  - `overall`（总体统计，遵循 `include_overall`）
  - `groups.primary`（小学组统计，引用 `SignApplicationStatsSummary`）
  - `groups.middle`（中学组统计，引用 `SignApplicationStatsSummary`）

> 说明：以上模型已在 `apifox_import_full.json` 的 `components.schemas` 中定义，路径已在 `paths` 中新增。

### 4. 成功响应示例（200）

```json
{
  "overall": { "total": 18, "rejected": 3, "pending": 5, "approved": 10 },
  "groups": {
    "primary": { "total": 10, "rejected": 2, "pending": 3, "approved": 5 },
    "middle": { "total": 8, "rejected": 1, "pending": 2, "approved": 5 }
  }
}
```

### 5. 调试示例（Apifox 与 Windows cURL）

- Apifox Query 示例：
  - `start_date=2025-01-01`
  - `end_date=2025-12-31`
  - `groups=primary`
  - `groups=middle`
  - `include_overall=true`

- 完整请求 URL 示例：
  - `http://localhost:8000/api/sign/applications/stats/?start_date=2025-01-01&end_date=2025-12-31&groups=primary&groups=middle&include_overall=true`

- Windows `curl.exe` 示例：

```powershell
curl.exe -G \
  -H "Authorization: Bearer <your_jwt_token>" \
  --data-urlencode "start_date=2025-01-01" \
  --data-urlencode "end_date=2025-12-31" \
  --data-urlencode "groups=primary" \
  --data-urlencode "groups=middle" \
  --data-urlencode "include_overall=true" \
  "http://localhost:8000/api/sign/applications/stats/"
```

### 6. 统计口径与规范一致性

- 按用户维度统计：默认统计“当前认证用户”的报名情况。
- 分组维度：`performance_group`（小学组 `primary`、中学组 `middle`）。
- 指标口径：
  - `total`：符合过滤条件的报名总数。
  - `rejected`：状态为 `rejected` 的数量。
  - `pending`：状态为 `pending` 的数量。
  - `approved`：状态为 `approved` 的数量。
- 规范一致性：该接口为“复杂筛选”的独立特定接口，符合《查询接口规范.md》与本文件的 GET 规则。

### 7. 扩展建议（可选）

- 管理员视角：如需统计指定用户，可增设 `user_id` Query 参数，并在权限上区分普通用户与管理员。
- 性能优化：建议为 `applicant`、`status`、`performance_group`、`create_time` 建立复合索引；统计查询使用聚合（annotate/count）。

---

## 📌 示例与规范补充：项目市场接口（GET）

本节新增两个项目市场接口示例，遵循“GET 使用集合路径 + Query 传参”的规范。

### 1. 接口定义（Apifox 导入）

- 获取项目市场列表
  - 路径：`/api/market/projects/list`
  - 方法：`GET`
  - 参数位置：`Query`
- 获取项目市场详情
  - 路径：`/api/market/projects/detail`
  - 方法：`GET`
  - 参数位置：`Query`

### 2. Query 参数说明

- 列表接口参数：
  - `keyword`：字符串，可选；项目名称模糊搜索关键字。
  - `category`：字符串，可选；项目分类。
  - `page`：整数，可选；页码，默认 1。
  - `size`：整数，可选；每页数量，默认 20。
- 详情接口参数：
  - `project_id`：字符串，必填；待查询项目的唯一标识符。

### 3. 成功响应示例（200）

**项目市场列表**

```json
{
  "status": 200,
  "message": "获取成功",
  "data": {
    "projects": [
      {
        "projectImage": "https://example.com/project.png",
        "projectName": "AI 协作平台",
        "ownerName": "张三",
        "ownerAvatar": "https://example.com/avatar.png",
        "likeCount": 128,
        "tags": ["AI", "协作", "教育"]
      }
    ],
    "total": 1,
    "page": 1,
    "size": 20
  }
}
```

**项目市场详情**

```json
{
  "status": 200,
  "message": "获取成功",
  "data": {
    "id": "b2c3d4e5",
    "name": "AI 协作平台",
    "description": "项目简介",
    "detailedDescription": "项目详细介绍",
    "category": "教育",
    "coverUrl": "https://example.com/project.png",
    "type": "web",
    "currentModule": "alpha",
    "status": "active",
    "progress": 60,
    "visibility": "public",
    "allowFork": true,
    "createdAt": "2025-01-01T12:00:00",
    "updatedAt": "2025-01-10T12:00:00",
    "parentId": null,
    "ownerId": 10001,
    "ownerName": "张三",
    "ownerAvatar": "https://example.com/avatar.png",
    "teamId": "123456",
    "teamName": "创新小组",
    "teamAvatar": "https://example.com/team.png",
    "teamIsPersonal": false,
    "teamSize": 5,
    "likeCount": 128,
    "tags": ["AI", "协作", "教育"]
  }
}
```

### 4. 调试示例（Apifox 与 Windows cURL）

- 项目市场列表（Apifox Query）：
  - `keyword=AI`
  - `category=教育`
  - `page=1`
  - `size=20`
- 项目市场详情（Apifox Query）：
  - `project_id=b2c3d4e5`

- Windows `curl.exe` 示例：

```powershell
curl.exe -G \
  --data-urlencode "keyword=AI" \
  --data-urlencode "category=教育" \
  --data-urlencode "page=1" \
  --data-urlencode "size=20" \
  "http://localhost:8000/api/market/projects/list"
```

```powershell
curl.exe -G \
  --data-urlencode "project_id=b2c3d4e5" \
  "http://localhost:8000/api/market/projects/detail"
```

### 5. 规范一致性说明

- 列表与详情均采用集合路径，不在 Path 中放置 ID。
- 项目详情的 `project_id` 通过 Query 传递，便于 Apifox 测试与复用。
