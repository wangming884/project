# 宝塔部署保姆级教程（从 0 到可上线）

> 适用项目：校园综合服务平台（本仓库）  
> 适用人群：第一次部署 Java + 前端静态站点的新手  
> 教程目标：1 台云服务器 + 1 个域名，完成前后端上线、HTTPS、生效验证、常见故障排查

---

## 0. 你将得到什么结果

完成后你会得到：

1. 前端访问地址：`https://你的域名`
2. 后端 API 地址：`https://你的域名/api/...`
3. 宝塔管理地址：`https://服务器IP:宝塔端口`
4. 可稳定重启的后端服务（PM2 托管）

本项目前端在生产环境默认请求 `/api`，所以推荐“同域名部署”（最省心，不用改前端 API 地址）。

---

## 1. 部署前准备（必须）

## 1.1 服务器要求

1. 系统：Ubuntu 20+/Debian 11+/CentOS 7+
2. 配置：最低 2C2G，推荐 2C4G
3. 磁盘：至少 20GB
4. 已有 root 或 sudo 权限

## 1.2 域名要求

1. 已购买域名
2. 域名已备案（中国大陆服务器必须）
3. DNS 已解析 `A 记录` 到服务器公网 IP

建议至少配置：

1. `@` -> 服务器 IP
2. `www` -> 服务器 IP（可选）

## 1.3 需要放行的端口

1. `22`（SSH）
2. `80`（HTTP）
3. `443`（HTTPS）
4. 宝塔面板端口（默认 8888，建议安装后改掉）

不要对公网开放：`3306`（MySQL）、`6379`（Redis）、`8080`（Java）。

---

## 2. 安装宝塔面板

先 SSH 登录服务器：

```bash
ssh root@你的服务器IP
```

按系统执行安装命令：

### Ubuntu/Debian

```bash
wget -O install.sh https://download.bt.cn/install/install-ubuntu_6.0.sh && bash install.sh ed8484bec
```

### CentOS

```bash
yum install -y wget && wget -O install.sh https://download.bt.cn/install/install_6.0.sh && sh install.sh ed8484bec
```

安装成功后会输出：

1. 面板访问地址（含安全入口路径）
2. 面板用户名
3. 面板密码

把这三项立即保存到密码管理器。

---

## 3. 首次登录宝塔后的初始化

按顺序做：

1. 登录面板。
2. 先改面板端口（不要一直用 8888）。
3. 启用面板 HTTPS。
4. 在宝塔“安全”里设置登录白名单（你的办公 IP）。

这一步是为了防止面板被扫端口暴力攻击。

---

## 4. 在宝塔安装运行环境

进入“软件商店”，安装：

1. `Nginx`（推荐稳定版）
2. `MySQL 8.0`
3. `Redis 7.x`
4. `OpenJDK 17`
5. `PM2`（进程托管）
6. `Maven`（若你打算服务器端构建；如果本地打包上传 jar 可不装）

安装后先确认都在“运行中”。

---

## 5. 上传项目代码

推荐目录结构：

1. 前端目录：`/www/wwwroot/campus-web`
2. 后端目录：`/www/wwwroot/campus-backend`

把本仓库上传后，保证：

1. 前端 HTML 在 `campus-web/` 根目录
2. Java 项目在 `campus-backend/backend-java/`

如果你上传的是整个仓库，这个结构会天然满足。

---

## 6. 配置 MySQL 数据库

在宝塔“数据库”执行：

1. 新建数据库：`campus_platform`
2. 新建用户：例如 `campus`
3. 设置强密码
4. 权限选择“本地服务器”

然后导入表结构文件：

`/www/wwwroot/campus-backend/backend-java/sql/schema.sql`

导入后应有 5 张表：

1. `users`
2. `points_history`
3. `checkin_records`
4. `secondhand_products`
5. `substitute_tasks`

---

## 7. 配置后端生产参数

编辑：

`/www/wwwroot/campus-backend/backend-java/src/main/resources/application-prod.yml`

至少修改：

1. `spring.datasource.url`
2. `spring.datasource.username`
3. `spring.datasource.password`
4. `spring.data.redis.password`（如果你设置了 Redis 密码）
5. `jwt.secret`（必须换成强密钥）

关键注意：

1. 本项目后端 context-path 是 `/api`。
2. 前端生产默认请求 `/api`，无需改 `js/api-config.js`。

---

## 8. 构建后端 Jar

进入后端目录：

```bash
cd /www/wwwroot/campus-backend/backend-java
```

构建：

```bash
mvn clean package -DskipTests
```

构建成功后会得到：

`target/campus-platform-1.0.0.jar`

如果你服务器没装 Maven：

1. 在本地构建好 jar 后上传到服务器
2. 也可以直接安装 Maven 再构建

---

## 9. 启动后端（PM2 托管）

在后端目录创建启动脚本：

```bash
cat > start.sh <<'EOF'
#!/bin/bash
java -Xms512m -Xmx1024m \
  -Dspring.profiles.active=prod \
  -jar target/campus-platform-1.0.0.jar
EOF
chmod +x start.sh
```

用 PM2 启动：

```bash
pm2 start ./start.sh --name campus-backend
pm2 save
pm2 startup
```

检查状态：

```bash
pm2 status
pm2 logs campus-backend --lines 100
```

本地连通性测试：

```bash
curl http://127.0.0.1:8080/api/auth/check
```

有 JSON 返回就表示后端可用。

---

## 10. 部署前端站点

在宝塔“网站”中：

1. 添加站点，域名填你的主域名
2. 根目录指向：`/www/wwwroot/campus-web`
3. PHP 版本选“纯静态”

确认首页文件（如 `index.html`）在该目录中。

---

## 11. 配置 Nginx 反向代理（最关键）

因为前端请求 `/api`，所以需要在前端站点 Nginx 配置里加下面内容：

```nginx
location /api/ {
    proxy_pass http://127.0.0.1:8080/api/;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}

location / {
    try_files $uri $uri/ /index.html;
}

location ~* \.(css|js|jpg|jpeg|png|gif|svg|ico|woff|woff2)$ {
    expires 7d;
    add_header Cache-Control "public";
}
```

保存后点击“重载 Nginx”。

---

## 12. 申请 SSL 并开启 HTTPS

在宝塔站点设置 -> SSL：

1. 选择 `Let's Encrypt`
2. 勾选域名
3. 申请证书
4. 开启“强制 HTTPS”

如果失败，先检查：

1. DNS 是否生效
2. 80 端口是否放行
3. 域名是否被 CDN 代理且回源异常

---

## 13. 一次性验收清单（照着点）

浏览器验证：

1. `https://你的域名` 可访问
2. 页面功能按钮可点击
3. 前端请求无明显报错

接口验证：

```bash
curl -I https://你的域名
curl https://你的域名/api/auth/check
```

服务验证：

```bash
pm2 status
pm2 logs campus-backend --lines 50
```

数据库验证：

```bash
mysql -u campus -p -e "use campus_platform; show tables;"
```

---

## 14. 常见报错与处理

## 14.1 页面能打开，但接口全 404

原因通常是 `/api` 反代没配好。检查：

1. Nginx 是否有 `location /api/`
2. `proxy_pass` 是否为 `http://127.0.0.1:8080/api/`
3. 后端是否真的监听在 8080

## 14.2 接口 502 Bad Gateway

说明 Nginx 找不到后端。执行：

```bash
pm2 status
pm2 logs campus-backend --lines 200
curl http://127.0.0.1:8080/api/auth/check
```

## 14.3 后端启动失败

重点看：

1. 数据库密码
2. Redis 密码
3. Java 版本是否 17
4. `application-prod.yml` 是否配置正确

## 14.4 登录成功后又掉线

重点看：

1. `jwt.secret` 是否被修改过且格式正确
2. 系统时间是否准确（服务器时钟漂移会导致 token 异常）

## 14.5 内存不足导致频繁重启

先把 JVM 调小：

```bash
# start.sh 中改为
-Xms256m -Xmx512m
```

2C2G 机器建议先用这个参数跑稳再扩容。

---

## 15. 生产安全最小实践（建议立刻做）

1. 宝塔面板端口改成非常规端口。
2. 面板只允许固定 IP 登录。
3. MySQL/Redis 只监听本机，不开放公网。
4. 定期备份数据库（每天至少一次）。
5. `jwt.secret` 使用 32 位以上随机强密钥。
6. 开启 HTTPS 强制跳转。

---

## 16. 后续运维命令速查

重启后端：

```bash
pm2 restart campus-backend
```

查看实时日志：

```bash
pm2 logs campus-backend
```

查看 Nginx 错误日志：

```bash
tail -f /www/wwwlogs/你的域名.error.log
```

重新构建并发布（后端）：

```bash
cd /www/wwwroot/campus-backend/backend-java
mvn clean package -DskipTests
pm2 restart campus-backend
```

---

## 17. 新手最容易踩的 5 个坑（提前避开）

1. 只部署了前端，忘记启动后端。
2. 后端启动了，但 Nginx 没配 `/api` 反代。
3. 数据库没导入 `schema.sql`。
4. `application-prod.yml` 还是默认密码。
5. SSL 没开强制 HTTPS，导致混合内容或回调异常。

---

## 18. 结论

按本文顺序做，你可以在一次部署中完成：

1. 前端静态站点上线
2. Java 后端稳定运行
3. `/api` 同域联通
4. HTTPS 与基础安全加固

如果你愿意，我下一步可以继续给你补一份“宝塔一键回滚脚本教程”（更新失败时 3 分钟恢复）。
