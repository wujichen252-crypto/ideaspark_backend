# 宝塔服务器部署指南（Spring Boot 后端 + 可选前端）

## 目标
- 将后端 Spring Boot 项目部署到宝塔（BTPanel）服务器，并通过 Nginx 对外提供 HTTP/HTTPS 服务
- 首次启动自动执行 Flyway 数据库迁移（当前版本 v14）
- 可选同时部署前端（Vite 构建产物），实现前后端联通

## 前置条件
- 一台已安装宝塔的 Linux 服务器（CentOS/Ubuntu），并已在宝塔中安装 Nginx
- 已准备域名并解析到服务器公网 IP；防火墙开放 80/443（以及后端预览端口，如 8080）
- 安装 Java 17（建议在宝塔“软件商店”或命令行安装）
- MySQL 数据库已可访问（本项目支持通过环境变量连接远程 MySQL）

## 1. 获取代码与构建后端
1) 登录服务器，创建目录：
```bash
sudo mkdir -p /www/wwwroot/ideaspark-backend
sudo chown -R www:www /www/wwwroot/ideaspark-backend
```
2) 通过 Git 或上传压缩包，把后端代码放到上述目录：
```bash
cd /www/wwwroot/ideaspark-backend
# 示例：使用 git
git clone <你的仓库地址> .
```
3) 构建可执行 JAR（两种方式）
- 方式 A（推荐，本地构建后上传）：
  - 在本地 Windows 开发机中执行：`mvn -q -DskipTests package`
  - 将 `target/ideaspark-0.0.1-SNAPSHOT.jar` 上传到服务器 `/www/wwwroot/ideaspark-backend/`
- 方式 B（服务器直接构建）：
  - 服务器安装 Maven 后执行：
```bash
cd /www/wwwroot/ideaspark-backend
mvn -q -DskipTests package
```

> 注意：从 v14 起，application.yml 不再内置数据库默认值，必须通过环境变量提供 DB_URL/DB_USERNAME/DB_PASSWORD/JWT_SECRET，否则启动会报错或被宝塔插件识别为“本地数据库默认配置”。

## 2. 配置环境变量（数据库与应用密钥）
本项目通过环境变量读取数据库连接与 JWT 密钥：
- DB_URL 例如：`jdbc:mysql://62.234.128.72:3306/ideaspark?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai`
- DB_USERNAME 例如：`ideaspark`
- DB_PASSWORD 例如：`<你的密码>`
- JWT_SECRET 建议设置为长度较长的随机字符串

你可以使用两种方式传递环境变量：
- 方式 A：systemd 单元文件中使用 `Environment=`（推荐）
- 方式 B：运行参数传入 `-DDB_URL=...`（不推荐，可能出现在进程列表中）

## 3. 使用 systemd 后台运行后端（推荐）
1) 创建 systemd 单元文件：
```bash
sudo tee /etc/systemd/system/ideaspark.service >/dev/null <<'EOF'
[Unit]
Description=IdeaSpark Spring Boot Service
After=network.target

[Service]
User=www
WorkingDirectory=/www/wwwroot/ideaspark-backend
Environment=DB_URL=jdbc:mysql://62.234.128.72:3306/ideaspark?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
Environment=DB_USERNAME=ideaspark
Environment=DB_PASSWORD=<请填入你的密码>
Environment=JWT_SECRET=<请填入一个足够长的密钥>
ExecStart=/usr/bin/java -jar ideaspark-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=5
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
EOF
```
2) 启动并设为开机自启：
```bash
sudo systemctl daemon-reload
sudo systemctl enable ideaspark
sudo systemctl start ideaspark
sudo systemctl status ideaspark
```
3) 查看日志（排错用）：
```bash
journalctl -u ideaspark -f
```

## 4. 在宝塔配置 Nginx 反向代理
1) 在宝塔创建站点（绑定你的域名，开启 HTTPS 并申请 Let’s Encrypt 证书）
2) 在站点设置中开启“反向代理”，目标：`http://127.0.0.1:8080/`
3) 或手动在 Nginx 站点配置中加入：
```nginx
location / {
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_pass http://127.0.0.1:8080;
}
```
4) 应用配置并重载 Nginx

> 如果你使用“Java 项目管理器”插件运行 JAR：请在“环境变量/启动参数”中显式设置 DB_URL/DB_USERNAME/DB_PASSWORD/JWT_SECRET；由于 application.yml 不再包含默认本地数据库，插件不会再提示“连接到本地 mysql”的警告。

## 5. 首次启动的数据库迁移校验
- 应用启动时会自动执行 Flyway 迁移，写入 `flyway_schema_history` 表
- 验证方式（MySQL 客户端）：
```sql
SELECT version, description, success, installed_on
FROM ideaspark.flyway_schema_history
ORDER BY installed_rank DESC;
```
预期当前版本为 v14（含新增 users 表画像与通知偏好字段）

## 6. 部署前端（可选）
前端（Vue 3 + Vite）推荐使用 Nginx 直接托管静态资源：
1) 在前端项目执行构建：
```bash
pnpm install
pnpm build
# 产物位于 dist/
```
2) 将 dist/ 上传到 `/www/wwwroot/<你的前端站点目录>`
3) Nginx 站点根目录指向该目录；如需与后端联通，可将 `/api` 代理到后端：
```nginx
location /api/ {
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_pass http://127.0.0.1:8080/api/;
}
```

## 7. 常见问题排查
- 端口被占用：修改 `server.port` 或停止占用进程（宝塔面板查看进程）
- 数据库连接失败：
  - 检查 DB_URL、用户名密码、远程 MySQL 白名单与防火墙
  - 通过 `telnet <mysql-host> 3306` 验证连通性
- 迁移失败：
  - 查看 `journalctl -u ideaspark -f` 详细错误
  - 确认数据库版本为 MySQL 8.x，且不存在手工创建的冲突表或字段
- HTTPS 证书：
  - 宝塔申请并自动续期；务必开启强制 HTTPS（301）

### 宝塔提示“连接到本地 mysql 用户名/密码可能错误”
- 现象：插件扫描 JAR 的 application.yml，检测到默认本地连接或空密码
- 解决：
  1) 升级到当前版本（v14 及以上），application.yml 已移除默认本地数据库占位
  2) 在“环境变量/启动参数”中设置：
     - `DB_URL=jdbc:mysql://<你的MySQL主机>:3306/ideaspark?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai`
     - `DB_USERNAME=<你的用户名>`
     - `DB_PASSWORD=<你的密码>`
     - `JWT_SECRET=<随机长字符串>`
  3) 重启项目后该提示消失

## 8. 升级与回滚
- 升级 JAR：
```bash
sudo systemctl stop ideaspark
cp /路径/新的jar /www/wwwroot/ideaspark-backend/ideaspark-0.0.1-SNAPSHOT.jar
sudo systemctl start ideaspark
```
- 如需回滚，替换为旧 JAR 后重启服务

## 9. 备用方案：使用宝塔“Java 项目管理器”插件
若你安装了该插件，可通过可视化界面管理 JAR：
1) 上传 JAR，入口选择 `java -jar ideaspark-0.0.1-SNAPSHOT.jar`
2) 在“环境变量/启动参数”处设置：
   - 推荐环境变量：`DB_URL/DB_USERNAME/DB_PASSWORD/JWT_SECRET`
   - 或参数：`-DDB_URL=... -DDB_USERNAME=... -DDB_PASSWORD=... -DJWT_SECRET=...`
3) 启动项目并在 Nginx 站点配置中做反向代理

## 10. 安全建议
- 不要把数据库密码与密钥写入代码或 Nginx 配置；统一用环境变量
- 后端仅对内网监听（如需），Nginx 对外暴露，减少直接攻击面
- 开启宝塔安全防护与日志监控，定期更新依赖与系统补丁

---
完成以上步骤后，你的后端服务将通过 Nginx 域名正常对外提供 API，首次启动已自动完成数据库迁移。如需我远程协助验证或完善 Nginx/SSL 配置，可在文档执行到相应步骤后告知我现状与域名。 

