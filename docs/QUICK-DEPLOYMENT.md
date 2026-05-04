# 快速部署指南

## 🚀 5分钟快速部署

本指南帮助您快速部署校园综合服务平台到宝塔面板。

---

## 📋 前置条件

- ✅ 一台云服务器（2核4G以上）
- ✅ CentOS 7+ 或 Ubuntu 18+ 系统
- ✅ 已开放端口：22, 80, 443, 8888

---

## 🎯 部署步骤

### 第一步：安装宝塔面板（5分钟）

```bash
# CentOS 系统
yum install -y wget && wget -O install.sh https://download.bt.cn/install/install_6.0.sh && sh install.sh ed8484bec

# Ubuntu 系统
wget -O install.sh https://download.bt.cn/install/install-ubuntu_6.0.sh && sudo bash install.sh ed8484bec
```

**记录安装完成后显示的面板地址、用户名和密码！**

---

### 第二步：安装运行环境（10分钟）

登录宝塔面板后，在 **"软件商店"** 安装：

1. **Nginx** 1.22+
2. **MySQL** 8.0
3. **Redis** 7.0+
4. **Java** 17
5. **PM2** 管理器

---

### 第三步：创建数据库（2分钟）

1. 点击 **"数据库"** → **"添加数据库"**
2. 数据库名：`campus_platform`
3. 用户名：`campus`
4. 密码：设置强密码
5. 访问权限：`本地服务器`

---

### 第四步：部署后端（10分钟）

#### 1. 上传代码

```bash
cd /www/wwwroot
mkdir campus-backend
cd campus-backend

# 上传 backend-java 文件夹的所有文件到这里
```

#### 2. 导入数据库

```bash
mysql -u campus -p campus_platform < sql/schema.sql
```

#### 3. 修改配置

编辑 `src/main/resources/application-prod.yml`：

```yaml
spring:
  datasource:
    username: campus
    password: 你的数据库密码
  data:
    redis:
      password: 你的Redis密码（如果有）
jwt:
  secret: 修改为长密钥（至少32位）
```

#### 4. 编译打包

```bash
# 如果没有 Maven，先安装
wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz
tar -xzf apache-maven-3.9.5-bin.tar.gz
export PATH=$PWD/apache-maven-3.9.5/bin:$PATH

# 编译
mvn clean package -DskipTests
```

#### 5. 启动应用

```bash
# 创建启动脚本
cat > start.sh << 'EOF'
#!/bin/bash
java -jar \
  -Xms512m \
  -Xmx1024m \
  -Dspring.profiles.active=prod \
  target/campus-platform-1.0.0.jar
EOF

chmod +x start.sh

# 使用 PM2 启动
pm2 start start.sh --name campus-backend
pm2 save
pm2 startup
```

#### 6. 配置反向代理

在宝塔面板：

1. **"网站"** → **"添加站点"**
2. 域名：`api.yourdomain.com`（或IP）
3. **"设置"** → **"反向代理"** → **"添加"**
4. 目标URL：`http://127.0.0.1:8080`

---

### 第五步：部署前端（5分钟）

#### 1. 创建网站

1. **"网站"** → **"添加站点"**
2. 域名：`www.yourdomain.com`（或IP）
3. 根目录：`/www/wwwroot/campus-frontend`
4. PHP版本：纯静态

#### 2. 上传文件

上传所有前端文件到 `/www/wwwroot/campus-frontend`：
- `index.html`
- `main.html`
- `*.html`
- `js/` 文件夹

#### 3. 修改API地址

编辑 `js/api-config.js`：

```javascript
const API_CONFIG = {
    BASE_URL: 'http://api.yourdomain.com/api',  // 修改为实际地址
    TIMEOUT: 10000
};
```

---

### 第六步：配置SSL（5分钟）

1. 点击站点 **"设置"** → **"SSL"**
2. 选择 **"Let's Encrypt"**
3. 勾选域名
4. 点击 **"申请"**
5. 开启 **"强制HTTPS"**

---

## ✅ 验证部署

### 测试后端

```bash
curl http://localhost:8080/api/auth/check
```

### 测试前端

浏览器访问：
```
http://your_ip
```

### 测试完整流程

1. 打开网站
2. 注册账号
3. 登录系统
4. 测试各个功能

---

## 🎉 部署完成！

### 访问地址

- **前端**: http://your_ip 或 https://www.yourdomain.com
- **后端**: http://your_ip/api 或 https://api.yourdomain.com/api
- **宝塔**: http://your_ip:8888

### 常用命令

```bash
# 查看后端状态
pm2 status

# 查看后端日志
pm2 logs campus-backend

# 重启后端
pm2 restart campus-backend

# 查看数据库
mysql -u campus -p campus_platform
```

---

## 🐛 遇到问题？

### 后端启动失败

```bash
# 查看详细日志
pm2 logs campus-backend --lines 100

# 检查端口
netstat -tunlp | grep 8080

# 检查数据库连接
mysql -u campus -p campus_platform
```

### 前端无法访问后端

1. 检查 `js/api-config.js` 中的 API 地址
2. 检查 Nginx 反向代理配置
3. 检查防火墙规则

### 需要详细教程？

查看完整部署教程：`BAOTA-DEPLOYMENT-GUIDE.md`

---

## 📞 技术支持

- 📖 完整文档: `BAOTA-DEPLOYMENT-GUIDE.md`
- 📋 项目文档: `README.md`
- 🚀 快速开始: `QUICK-START.md`

---

**祝您部署顺利！🎉**
