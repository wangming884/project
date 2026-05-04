# 宝塔部署保姆级教程（校园综合服务平台）

适用版本：当前仓库（更新时间 2026-05-05）  
适用架构：`pages/` 静态前端 + `backend-java/` Spring Boot 后端

## 1. 最终效果

部署完成后你将得到：

1. 前端首页：`https://你的域名/pages/index.html`
2. 管理员登录页：`https://你的域名/pages/admin-login.html`
3. 代课大厅：`https://你的域名/pages/substitute.html`
4. 代课发布/我的任务：`https://你的域名/pages/substitute-manage.html`
5. API 地址：`https://你的域名/api/...`
6. 宝塔面板地址：`https://服务器IP:宝塔端口`

## 2. 准备工作

1. 云服务器（建议 2C4G，最低 2C2G）
2. 已解析到服务器的域名（`@` 和 `www`）
3. 已放行端口：`22`、`80`、`443`、宝塔端口
4. 不对公网暴露：`3306`、`6379`、`8080`

## 3. 安装宝塔

SSH 登录服务器后执行官方安装命令（按系统选择），安装成功后保存：

1. 面板地址
2. 账号
3. 密码

首次登录建议立即做：

1. 修改宝塔默认端口
2. 开启面板 HTTPS
3. 配置宝塔登录白名单

## 4. 安装运行环境（宝塔软件商店）

安装以下软件并确认“运行中”：

1. Nginx
2. MySQL 8.0
3. Redis 7.x
4. OpenJDK 17
5. PM2
6. Maven（可选，若在服务器构建 jar）

## 5. 上传代码与目录规范

推荐目录：

1. 前端：`/www/wwwroot/campus-web`
2. 后端：`/www/wwwroot/campus-backend`

上传本仓库后应保证：

1. 前端页面目录存在：`/www/wwwroot/campus-web/pages`
2. 后端目录存在：`/www/wwwroot/campus-backend/backend-java`
3. 代课页面存在：`/www/wwwroot/campus-web/pages/substitute.html`
4. 代课管理页存在：`/www/wwwroot/campus-web/pages/substitute-manage.html`

## 6. 初始化数据库

在宝塔新建 MySQL 数据库（例如 `campus_platform`），再导入：

`/www/wwwroot/campus-backend/backend-java/sql/schema.sql`

导入后至少应有：

1. `users`
2. `points_history`
3. `checkin_records`
4. `secondhand_products`
5. `substitute_tasks`

## 7. 配置后端生产参数（非常关键）

编辑文件：

`/www/wwwroot/campus-backend/backend-java/src/main/resources/application-prod.yml`

重点修改：

1. `spring.datasource.url`
2. `DB_USERNAME` / `DB_PASSWORD`（或直接写入 yml）
3. `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD`
4. `JWT_SECRET`（必须换强密钥）
5. `ADMIN_USERNAME` / `ADMIN_PASSWORD`（必须修改默认管理员账号）

Google Drive 预留参数（如暂不用可保留默认）：

1. `GOOGLE_DRIVE_ENABLED`
2. `GOOGLE_DRIVE_CLIENT_ID`
3. `GOOGLE_DRIVE_CLIENT_SECRET`
4. `GOOGLE_DRIVE_REFRESH_TOKEN`
5. `GOOGLE_DRIVE_MATERIALS_FOLDER_ID`
6. `GOOGLE_DRIVE_SOFTWARE_FOLDER_ID`

## 8. 构建后端

```bash
cd /www/wwwroot/campus-backend/backend-java
mvn clean package -DskipTests
```

产物：

`target/campus-platform-1.0.0.jar`

## 9. 启动后端（PM2 托管）

在后端目录创建启动脚本：

```bash
cat > start.sh <<'EOF'
#!/bin/bash
export SPRING_PROFILES_ACTIVE=prod
export DB_USERNAME=你的数据库用户名
export DB_PASSWORD=你的数据库密码
export REDIS_HOST=127.0.0.1
export REDIS_PORT=6379
export REDIS_PASSWORD=你的Redis密码
export JWT_SECRET=请替换为32位以上强密钥
export ADMIN_USERNAME=请替换管理员账号
export ADMIN_PASSWORD=请替换管理员密码

java -Xms512m -Xmx1024m -jar target/campus-platform-1.0.0.jar
EOF
chmod +x start.sh
```

使用 PM2 启动：

```bash
pm2 start ./start.sh --name campus-backend
pm2 save
pm2 startup
```

检查状态：

```bash
pm2 status
pm2 logs campus-backend --lines 100
curl http://127.0.0.1:8080/api/auth/check
```

## 10. 配置前端站点（宝塔网站）

1. 添加站点：填你的域名
2. 站点根目录：`/www/wwwroot/campus-web`
3. PHP 版本：纯静态

## 11. Nginx 配置（核心）

在站点 Nginx 配置中加入：

```nginx
location /api/ {
    proxy_pass http://127.0.0.1:8080/api/;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}

location = / {
    return 302 /pages/index.html;
}

location / {
    try_files $uri $uri/ =404;
}

location ~* \.(css|js|jpg|jpeg|png|gif|svg|ico|woff|woff2)$ {
    expires 7d;
    add_header Cache-Control "public";
}
```

保存后重载 Nginx。

## 12. SSL 证书与 HTTPS

宝塔站点设置 -> SSL：

1. 选择 Let's Encrypt
2. 申请证书
3. 开启强制 HTTPS

## 13. 上线验收清单

浏览器检查：

1. `https://你的域名/pages/index.html`
2. `https://你的域名/pages/admin-login.html`
3. `https://你的域名/pages/substitute.html`
4. `https://你的域名/pages/substitute-manage.html`
5. 登录后管理员后台可进入：`/pages/admin-dashboard.html`
6. 管理员登录后进入 `pages/substitute.html`，应自动切换为全量任务管理视图

接口检查：

```bash
curl -I https://你的域名
curl https://你的域名/api/auth/check
curl "https://你的域名/api/substitute/tasks?page=1&pageSize=5"
```

服务检查：

```bash
pm2 status
pm2 logs campus-backend --lines 50
```

数据库检查：

```bash
mysql -u 你的用户名 -p -e "use campus_platform; show tables;"
```

## 14. 常见问题

### 14.1 页面能打开，接口 404

检查 Nginx 是否配置了 `/api/` 反代到 `127.0.0.1:8080/api/`。

### 14.2 接口 502

后端未启动或崩溃，执行：

```bash
pm2 status
pm2 logs campus-backend --lines 200
```

### 14.3 管理员登录失败

检查 `ADMIN_USERNAME` / `ADMIN_PASSWORD` 是否与部署时设置一致。

### 14.4 代课大厅能打开，但任务列表为空或报权限

按顺序检查：

1. Nginx `/api/` 反代是否生效
2. 后端是否已经导入 `substitute_tasks` 表
3. 普通大厅使用的是 `GET /api/substitute/tasks`
4. 管理员全量视图使用的是 `GET /api/substitute/admin/tasks`
5. 管理员是否通过 `pages/admin-login.html` 登录并拿到管理员 token

### 14.5 学习资源上传返回“预留模式”

这是当前设计：已打通管理员上传接口，但默认是 Google Drive Stub。  
如需真实上传，需后续接入 Google Drive API 并填写完整凭据。

## 15. 运维常用命令

重启后端：

```bash
pm2 restart campus-backend
```

重新构建并发布：

```bash
cd /www/wwwroot/campus-backend/backend-java
mvn clean package -DskipTests
pm2 restart campus-backend
```

查看 Nginx 错误日志：

```bash
tail -f /www/wwwlogs/你的域名.error.log
```
