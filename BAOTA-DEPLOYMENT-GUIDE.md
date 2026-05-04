# 宝塔面板部署教程

## 📚 目录

- [环境准备](#环境准备)
- [安装宝塔面板](#安装宝塔面板)
- [配置服务器环境](#配置服务器环境)
- [部署数据库](#部署数据库)
- [部署后端应用](#部署后端应用)
- [部署前端网站](#部署前端网站)
- [配置域名和SSL](#配置域名和ssl)
- [性能优化](#性能优化)
- [常见问题](#常见问题)

---

## 🚀 环境准备

### 服务器要求

| 项目 | 最低配置 | 推荐配置 |
|------|----------|----------|
| **CPU** | 1核 | 2核+ |
| **内存** | 2GB | 4GB+ |
| **硬盘** | 20GB | 40GB+ |
| **带宽** | 1Mbps | 5Mbps+ |
| **系统** | CentOS 7+ / Ubuntu 18+ | CentOS 8+ / Ubuntu 20+ |

### 需要开放的端口

| 端口 | 用途 | 是否必须 |
|------|------|----------|
| 22 | SSH | ✅ 必须 |
| 80 | HTTP | ✅ 必须 |
| 443 | HTTPS | ✅ 必须 |
| 8888 | 宝塔面板 | ✅ 必须 |
| 3306 | MySQL | ⚠️ 建议仅内网 |
| 6379 | Redis | ⚠️ 建议仅内网 |
| 8080 | 后端应用 | ⚠️ 建议仅内网 |

---

## 📦 安装宝塔面板

### 1. 连接服务器

使用 SSH 工具（如 Xshell、FinalShell、PuTTY）连接服务器：

```bash
ssh root@your_server_ip
```

### 2. 安装宝塔面板

#### CentOS 系统

```bash
yum install -y wget && wget -O install.sh https://download.bt.cn/install/install_6.0.sh && sh install.sh ed8484bec
```

#### Ubuntu 系统

```bash
wget -O install.sh https://download.bt.cn/install/install-ubuntu_6.0.sh && sudo bash install.sh ed8484bec
```

#### Debian 系统

```bash
wget -O install.sh https://download.bt.cn/install/install-ubuntu_6.0.sh && bash install.sh ed8484bec
```

### 3. 等待安装完成

安装过程需要 5-10 分钟，完成后会显示：

```
==================================================================
Congratulations! Installed successfully!
==================================================================
外网面板地址: http://your_ip:8888/xxxxxxxx
内网面板地址: http://192.168.x.x:8888/xxxxxxxx
username: xxxxxxxx
password: xxxxxxxx
==================================================================
```

**⚠️ 重要：请立即保存这些信息！**

### 4. 登录宝塔面板

1. 在浏览器中访问：`http://your_ip:8888/xxxxxxxx`
2. 输入用户名和密码
3. 首次登录需要绑定宝塔账号（免费注册）

---

## 🔧 配置服务器环境

### 1. 安装软件环境

登录宝塔面板后，点击左侧菜单 **"软件商店"**，安装以下软件：

#### 必装软件

| 软件 | 版本 | 说明 |
|------|------|------|
| **Nginx** | 1.22+ | Web 服务器 |
| **MySQL** | 8.0+ | 数据库 |
| **Redis** | 7.0+ | 缓存 |
| **Java** | 17+ | Java 运行环境 |
| **PM2** | 最新版 | 进程管理器 |

#### 安装步骤

1. **安装 Nginx**
   - 在软件商店搜索 "Nginx"
   - 点击 "安装"
   - 选择 "编译安装"（推荐）或 "极速安装"
   - 等待安装完成

2. **安装 MySQL**
   - 搜索 "MySQL"
   - 选择版本 8.0
   - 点击 "安装"
   - 设置 root 密码（请记住！）
   - 等待安装完成（约 10-15 分钟）

3. **安装 Redis**
   - 搜索 "Redis"
   - 选择版本 7.0+
   - 点击 "安装"
   - 设置密码（可选但推荐）

4. **安装 Java**
   - 搜索 "Java"
   - 选择 OpenJDK 17
   - 点击 "安装"

5. **安装 PM2**
   - 搜索 "PM2"
   - 点击 "安装"

### 2. 配置防火墙

点击左侧菜单 **"安全"**，添加以下规则：

| 端口 | 协议 | 策略 | 备注 |
|------|------|------|------|
| 80 | TCP | 放行 | HTTP |
| 443 | TCP | 放行 | HTTPS |
| 8888 | TCP | 放行 | 宝塔面板 |
| 3306 | TCP | 拒绝 | MySQL（仅内网） |
| 6379 | TCP | 拒绝 | Redis（仅内网） |
| 8080 | TCP | 拒绝 | 后端应用（仅内网） |

**⚠️ 安全建议**：
- MySQL 和 Redis 不要对外开放
- 修改宝塔面板默认端口 8888
- 启用面板 SSL 和两步验证

---

## 🗄️ 部署数据库

### 1. 创建数据库

1. 点击左侧菜单 **"数据库"**
2. 点击 **"添加数据库"**
3. 填写信息：
   - 数据库名：`campus_platform`
   - 用户名：`campus`
   - 密码：设置一个强密码
   - 访问权限：`本地服务器`
4. 点击 **"提交"**

### 2. 导入数据库结构

#### 方法一：使用宝塔面板

1. 点击数据库名称后的 **"管理"** 按钮
2. 进入 phpMyAdmin
3. 点击 **"导入"**
4. 选择文件：`backend-java/sql/schema.sql`
5. 点击 **"执行"**

#### 方法二：使用命令行

```bash
# 上传 schema.sql 到服务器
cd /www/wwwroot

# 导入数据库
mysql -u campus -p campus_platform < schema.sql
```

### 3. 验证数据库

```bash
mysql -u campus -p
```

```sql
USE campus_platform;
SHOW TABLES;
-- 应该显示 5 张表
```

---

## 🚀 部署后端应用

### 1. 上传后端代码

#### 方法一：使用宝塔文件管理器

1. 点击左侧菜单 **"文件"**
2. 进入目录：`/www/wwwroot`
3. 创建文件夹：`campus-backend`
4. 点击 **"上传"**
5. 上传整个 `backend-java` 文件夹

#### 方法二：使用 Git

```bash
cd /www/wwwroot
git clone https://github.com/your-username/campus-platform.git
cd campus-platform/backend-java
```

### 2. 配置应用

编辑配置文件：

```bash
cd /www/wwwroot/campus-backend
vi src/main/resources/application-prod.yml
```

修改以下配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_platform?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: campus
    password: your_database_password  # 修改为实际密码
  
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password  # 如果设置了密码

jwt:
  secret: your-production-secret-key-must-be-at-least-256-bits-long  # 修改为生产环境密钥
```

### 3. 编译打包

```bash
cd /www/wwwroot/campus-backend

# 如果没有 Maven，先安装
wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz
tar -xzf apache-maven-3.9.5-bin.tar.gz
export PATH=/www/wwwroot/campus-backend/apache-maven-3.9.5/bin:$PATH

# 编译打包
mvn clean package -DskipTests
```

### 4. 使用 PM2 启动应用

#### 创建启动脚本

```bash
vi start.sh
```

添加内容：

```bash
#!/bin/bash
java -jar \
  -Xms512m \
  -Xmx1024m \
  -Dspring.profiles.active=prod \
  -Dserver.port=8080 \
  target/campus-platform-1.0.0.jar
```

```bash
chmod +x start.sh
```

#### 使用 PM2 启动

```bash
pm2 start start.sh --name campus-backend
pm2 save
pm2 startup
```

#### 查看运行状态

```bash
pm2 status
pm2 logs campus-backend
```

### 5. 配置 Nginx 反向代理

1. 点击左侧菜单 **"网站"**
2. 点击 **"添加站点"**
3. 填写信息：
   - 域名：`api.yourdomain.com`（或使用 IP）
   - 根目录：`/www/wwwroot/campus-backend`
4. 点击 **"提交"**

5. 点击站点名称后的 **"设置"**
6. 点击 **"反向代理"**
7. 点击 **"添加反向代理"**
8. 填写信息：
   - 代理名称：`backend`
   - 目标 URL：`http://127.0.0.1:8080`
   - 发送域名：`$host`
9. 点击 **"保存"**

### 6. 测试后端接口

```bash
curl http://localhost:8080/api/auth/check
```

或在浏览器访问：
```
http://your_ip/api/auth/check
```

---

## 🌐 部署前端网站

### 1. 创建网站

1. 点击左侧菜单 **"网站"**
2. 点击 **"添加站点"**
3. 填写信息：
   - 域名：`www.yourdomain.com`（或使用 IP）
   - 根目录：`/www/wwwroot/campus-frontend`
   - PHP 版本：纯静态
4. 点击 **"提交"**

### 2. 上传前端文件

1. 点击左侧菜单 **"文件"**
2. 进入目录：`/www/wwwroot/campus-frontend`
3. 上传所有前端文件：
   - `index.html`
   - `main.html`
   - `checkin.html`
   - `secondhand.html`
   - `substitute.html`
   - `js/` 文件夹
   - 其他 HTML 文件

### 3. 配置 API 地址

编辑 `js/api-config.js`：

```javascript
const API_CONFIG = {
    // 生产环境 API 地址
    BASE_URL: 'http://api.yourdomain.com/api',  // 修改为实际域名
    TIMEOUT: 10000
};
```

### 4. 配置 Nginx

点击站点 **"设置"** → **"配置文件"**，添加以下配置：

```nginx
location / {
    try_files $uri $uri/ /index.html;
}

# 静态资源缓存
location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
    expires 7d;
    add_header Cache-Control "public, immutable";
}

# Gzip 压缩
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/json;
```

### 5. 测试前端网站

在浏览器访问：
```
http://your_ip
```

或

```
http://www.yourdomain.com
```

---

## 🔐 配置域名和SSL

### 1. 域名解析

在域名服务商（如阿里云、腾讯云）添加 DNS 解析：

| 记录类型 | 主机记录 | 记录值 | TTL |
|----------|----------|--------|-----|
| A | @ | your_server_ip | 600 |
| A | www | your_server_ip | 600 |
| A | api | your_server_ip | 600 |

### 2. 申请 SSL 证书

#### 方法一：使用宝塔面板（推荐）

1. 点击站点 **"设置"**
2. 点击 **"SSL"**
3. 选择 **"Let's Encrypt"**
4. 勾选域名
5. 点击 **"申请"**
6. 等待申请完成
7. 开启 **"强制 HTTPS"**

#### 方法二：手动上传证书

1. 从证书服务商下载证书
2. 点击 **"其他证书"**
3. 粘贴证书内容
4. 点击 **"保存"**

### 3. 配置 HTTPS 跳转

在 Nginx 配置中添加：

```nginx
# HTTP 跳转到 HTTPS
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    return 301 https://$server_name$request_uri;
}
```

### 4. 测试 HTTPS

访问：
```
https://www.yourdomain.com
https://api.yourdomain.com/api/auth/check
```

---

## ⚡ 性能优化

### 1. 开启 Nginx 缓存

编辑站点配置文件：

```nginx
# 静态资源缓存
location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
    expires 30d;
    add_header Cache-Control "public, immutable";
}

# HTML 文件不缓存
location ~* \.html$ {
    expires -1;
    add_header Cache-Control "no-cache, no-store, must-revalidate";
}
```

### 2. 开启 Gzip 压缩

```nginx
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_comp_level 6;
gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/json application/javascript;
```

### 3. 配置 MySQL 优化

点击 **"数据库"** → **"性能调整"**：

```ini
[mysqld]
# 连接数
max_connections = 200

# 缓冲池大小（建议设置为内存的 50-70%）
innodb_buffer_pool_size = 1G

# 日志文件大小
innodb_log_file_size = 256M

# 查询缓存
query_cache_size = 64M
query_cache_type = 1
```

### 4. 配置 Redis 优化

编辑 Redis 配置：

```bash
vi /www/server/redis/redis.conf
```

```conf
# 最大内存
maxmemory 512mb

# 内存淘汰策略
maxmemory-policy allkeys-lru

# 持久化
save 900 1
save 300 10
save 60 10000
```

### 5. 配置 Java 应用优化

修改启动脚本：

```bash
java -jar \
  -Xms1024m \
  -Xmx2048m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Dspring.profiles.active=prod \
  target/campus-platform-1.0.0.jar
```

---

## 🔍 监控和日志

### 1. 查看应用日志

```bash
# PM2 日志
pm2 logs campus-backend

# 应用日志
tail -f /www/wwwroot/campus-backend/logs/campus-platform.log

# Nginx 访问日志
tail -f /www/wwwlogs/yourdomain.com.log

# Nginx 错误日志
tail -f /www/wwwlogs/yourdomain.com.error.log
```

### 2. 使用宝塔监控

1. 点击左侧菜单 **"监控"**
2. 查看 CPU、内存、磁盘、网络使用情况
3. 设置告警规则

### 3. 配置日志轮转

编辑 `/etc/logrotate.d/campus-backend`：

```
/www/wwwroot/campus-backend/logs/*.log {
    daily
    rotate 30
    missingok
    notifempty
    compress
    delaycompress
    copytruncate
}
```

---

## 🔄 自动化部署

### 1. 创建部署脚本

```bash
vi /www/wwwroot/campus-backend/deploy.sh
```

```bash
#!/bin/bash

echo "开始部署..."

# 停止应用
pm2 stop campus-backend

# 备份
cp target/campus-platform-1.0.0.jar backup/campus-platform-$(date +%Y%m%d_%H%M%S).jar

# 拉取最新代码
git pull origin main

# 编译打包
mvn clean package -DskipTests

# 启动应用
pm2 start campus-backend

echo "部署完成！"
```

```bash
chmod +x deploy.sh
```

### 2. 使用 Webhook 自动部署

1. 点击 **"软件商店"** → 搜索 **"宝塔WebHook"**
2. 安装插件
3. 添加 Hook：
   - 名称：`campus-deploy`
   - 执行脚本：`/www/wwwroot/campus-backend/deploy.sh`
4. 获取 Hook URL
5. 在 Git 仓库设置 Webhook

---

## 🐛 常见问题

### Q1: 后端启动失败

**检查步骤**：

```bash
# 查看日志
pm2 logs campus-backend

# 检查端口占用
netstat -tunlp | grep 8080

# 检查 Java 版本
java -version

# 检查数据库连接
mysql -u campus -p campus_platform
```

**常见原因**：
- 数据库密码错误
- 端口被占用
- Java 版本不对
- 内存不足

### Q2: 前端无法访问后端

**检查步骤**：

1. 检查 API 地址配置
2. 检查 Nginx 反向代理配置
3. 检查防火墙规则
4. 检查 CORS 配置

```bash
# 测试后端接口
curl http://localhost:8080/api/auth/check

# 查看 Nginx 错误日志
tail -f /www/wwwlogs/yourdomain.com.error.log
```

### Q3: SSL 证书申请失败

**解决方案**：

1. 确认域名已正确解析
2. 确认 80 端口可访问
3. 关闭 CDN 加速
4. 等待 DNS 生效（最多 24 小时）

### Q4: 数据库连接失败

**检查步骤**：

```bash
# 检查 MySQL 状态
systemctl status mysql

# 检查端口
netstat -tunlp | grep 3306

# 测试连接
mysql -u campus -p -h localhost campus_platform
```

### Q5: 内存不足

**解决方案**：

1. 升级服务器配置
2. 优化 Java 堆内存设置
3. 配置 Swap 分区

```bash
# 创建 2GB Swap
dd if=/dev/zero of=/swapfile bs=1M count=2048
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab
```

### Q6: 网站访问慢

**优化建议**：

1. 开启 Gzip 压缩
2. 配置静态资源缓存
3. 使用 CDN 加速
4. 优化数据库查询
5. 增加 Redis 缓存

---

## 📋 部署检查清单

### 部署前

- [ ] 服务器配置满足要求
- [ ] 域名已备案（国内服务器）
- [ ] 域名已解析到服务器
- [ ] 安全组/防火墙已配置
- [ ] 数据库密码已设置
- [ ] JWT 密钥已修改

### 部署中

- [ ] 宝塔面板已安装
- [ ] Nginx 已安装
- [ ] MySQL 已安装并配置
- [ ] Redis 已安装
- [ ] Java 17 已安装
- [ ] PM2 已安装
- [ ] 数据库已创建并导入
- [ ] 后端应用已编译
- [ ] 后端应用已启动
- [ ] 前端文件已上传
- [ ] Nginx 反向代理已配置

### 部署后

- [ ] 后端接口可访问
- [ ] 前端网站可访问
- [ ] 用户注册功能正常
- [ ] 用户登录功能正常
- [ ] SSL 证书已配置
- [ ] HTTPS 强制跳转已开启
- [ ] 日志记录正常
- [ ] 监控告警已配置
- [ ] 备份策略已设置

---

## 🎉 部署完成

恭喜！您已成功部署校园综合服务平台！

### 访问地址

- **前端网站**: https://www.yourdomain.com
- **后端API**: https://api.yourdomain.com/api
- **宝塔面板**: https://your_ip:8888

### 下一步

1. 配置定时备份
2. 设置监控告警
3. 优化性能
4. 添加 CDN 加速
5. 配置日志分析

### 技术支持

- 📧 邮箱: admin@mybrand.com
- 📖 文档: 查看项目文档
- 🐛 问题: 提交 GitHub Issues

---

**祝您使用愉快！🎉**
