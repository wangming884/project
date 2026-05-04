# 校园综合服务平台（Campus Platform）

本项目是一个校园综合服务平台，当前采用「静态前端 + Java 后端」架构，支持用户系统、积分体系、晚寝签到、二手交易、代课任务、学习资源与管理员后台。

最后更新：2026-05-04

## 1. 功能概览

### 用户侧

1. 用户注册/登录
2. 每日签到与积分记录
3. 晚寝签到（含审核流程）
4. 二手商品发布与浏览
5. 代课任务发布与接单
6. 学习资料/学习软件下载入口
7. 联系作者

### 管理员侧

1. 管理员独立登录页：`pages/admin-login.html`
2. 管理后台：`pages/admin-dashboard.html`
3. 签到审核与强制状态修改
4. 用户积分增加/扣除
5. 用户启用/禁用
6. 重置用户签到连续记录
7. 查看用户积分流水
8. 管理员上传学习资料/学习软件（当前为 Google Drive 预留上传会话）

## 2. 技术栈

1. 前端：HTML + CSS + JavaScript（静态页面）
2. 后端：Spring Boot 3 + Spring Security + JWT
3. 数据库：MySQL 8
4. 缓存：Redis
5. ORM：MyBatis-Plus

## 3. 当前项目结构

```text
.
├── backend-java/                  # Java 后端
│   ├── sql/schema.sql
│   └── src/main/...
├── pages/                         # 前端页面
│   ├── index.html
│   ├── main.html
│   ├── checkin.html
│   ├── secondhand.html
│   ├── substitute.html
│   ├── resources.html
│   ├── learning_materials.html
│   ├── learning_software.html
│   ├── admin-login.html
│   ├── admin-dashboard.html
│   └── ...
├── js/
│   ├── api-config.js
│   ├── api-utils.js
│   └── admin-auth.js
├── docs/                          # 其余项目文档
├── BAOTA-DEPLOYMENT-GUIDE.md      # 宝塔部署教程
└── README.md
```

## 4. 本地运行（开发）

### 4.1 后端启动

```bash
cd backend-java
mvn clean compile
mvn spring-boot:run
```

默认地址：

1. 后端：`http://localhost:8080`
2. API 前缀：`/api`

### 4.2 前端启动

将仓库目录作为静态站点根目录启动即可（例如 VS Code Live Server / Nginx / 任意静态服务器）。

前端默认行为：

1. `localhost` 下请求 `http://localhost:3000/api`（可按需调整 `js/api-config.js`）
2. 非 `localhost` 下请求同域 `/api`

## 5. 管理员账号说明

管理员账号来自后端配置文件：

1. 开发环境：`backend-java/src/main/resources/application.yml`
2. 生产环境：`backend-java/src/main/resources/application-prod.yml`（支持环境变量覆盖）

默认配置（上线前必须修改）：

1. `admin.username=admin`
2. `admin.password=Admin@123456`

## 6. 学习资源与上传说明

当前学习资源模块已预留 Google Drive 接口，包含：

1. 资源列表
2. 下载链接生成
3. 上传会话创建（管理员后台可操作）

注意：当前是预留实现（Stub），返回标准化结构，后续可无缝替换为真实 Google Drive API 调用。

## 7. 关键接口（节选）

1. 管理员登录：`POST /api/auth/admin-login`
2. 管理员调整积分：`POST /api/points/admin/adjust`
3. 管理员启用/禁用用户：`POST /api/points/admin/users/{userId}/status`
4. 管理员重置签到：`POST /api/points/admin/users/{userId}/reset-signin`
5. 管理员积分流水：`GET /api/points/admin/history`
6. 管理员上传资料：`POST /api/resources/admin/materials/upload`
7. 管理员上传软件：`POST /api/resources/admin/software/upload`

完整接口请查看：[docs/API-DOCUMENTATION.md](/e:/project/docs/API-DOCUMENTATION.md)

## 8. 部署说明

生产部署请直接参考：

[BAOTA-DEPLOYMENT-GUIDE.md](/e:/project/BAOTA-DEPLOYMENT-GUIDE.md)

该文档已按当前项目结构（`pages/` + `backend-java/`）更新。
