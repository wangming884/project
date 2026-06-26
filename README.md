# 校园综合服务平台（Campus Platform）

本项目是一个校园综合服务平台，当前采用「静态前端 + Java 后端」架构，支持用户系统、积分体系、晚寝签到、代刷课、二手交易、代课任务、学习资源与管理员后台。

最后更新：2026-05-05

## 1. 功能概览

### 用户侧

1. 用户注册/登录
2. 每日签到与积分记录
3. 晚寝签到（含审核流程）
4. 二手商品发布与浏览
5. 二手“我的发布”管理页：`pages/secondhand-manage.html`
6. 代课任务发布与接单
7. 代刷课积分下单页：`pages/course-brush.html`
8. 学习资料/学习软件下载入口
9. 联系作者
10. 代课“我的发布 / 我的接单”管理页：`pages/substitute-manage.html`

### 管理员侧

1. 管理员通过首页登录弹窗进入后台：`pages/index.html`
2. 管理后台：`pages/admin-dashboard.html`
3. 签到审核与强制状态修改
4. 用户积分增加/扣除
5. 用户启用/禁用
6. 重置用户签到连续记录
7. 查看用户积分流水
8. 管理员上传学习资料/学习软件（当前为 Google Drive 预留上传会话）
9. 代课大厅全量任务查看
10. 代课任务强制状态流转（`pending / accepted / completed / cancelled`）
11. 代刷课课程管理（新增课程、调整积分、启停课程）
12. 代刷课订单查看与状态流转

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
│   ├── course-brush.html
│   ├── secondhand.html
│   ├── substitute.html
│   ├── substitute-manage.html
│   ├── resources.html
│   ├── learning_materials.html
│   ├── learning_software.html
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

1. `localhost` / `127.0.0.1` 下默认请求 `http://localhost:8080/api`
2. 非本地环境默认请求同域 `/api`
3. 如需切换到 Node 示例后端（`3000` 端口）或其他地址，可通过以下任一方式覆盖：
   `?apiBaseUrl=http://localhost:3000/api`
   或在浏览器控制台执行 `localStorage.setItem('apiBaseUrl', 'http://localhost:3000/api')`

## 5. 管理员账号说明

管理员账号来自后端配置文件：

1. 开发环境：`backend-java/src/main/resources/application.yml`
2. 生产环境：`backend-java/src/main/resources/application-prod.yml`（支持环境变量覆盖）

默认配置（上线前必须修改）：

1. `admin.username=admin`
2. `admin.password=Admin@123456`

当前管理员能力说明：

1. 在首页登录弹窗输入管理员账号后，后端通过 `POST /api/auth/login` 签发管理员专属 JWT，业务层以保留身份 `userId=0` 识别超级权限
2. 前端 `pages/substitute.html` 会自动切换到管理员视图，展示全量代课任务
3. 管理员可直接在代课大厅强制修改任务状态
4. `pages/substitute-manage.html` 会自动锁定管理员的普通发布入口，避免误以超级身份发布用户任务

## 6. 代课模块当前状态

当前代课模块已接入 Java 后端，不再使用前端本地草稿：

1. 大厅页：`pages/substitute.html`
2. 发布/我的任务页：`pages/substitute-manage.html`
3. 公开任务列表接口：`GET /api/substitute/tasks`
4. 普通用户任务发布接口：`POST /api/substitute/publish`
5. 我的发布：`GET /api/substitute/my-published`
6. 我的接单：`GET /api/substitute/my-accepted`
7. 任务统计：`GET /api/substitute/statistics`
8. 管理员全量任务：`GET /api/substitute/admin/tasks`
9. 管理员强制改状态：`POST /api/substitute/admin/tasks/{taskId}/status`

## 6.5 二手模块当前状态

当前二手模块已接入 Java 后端，前台与管理页都可使用真实数据：

1. 大厅页：`pages/secondhand.html`
2. 我的发布管理页：`pages/secondhand-manage.html`
3. 发布商品接口：`POST /api/secondhand/publish`
4. 我的发布：`GET /api/secondhand/my-products`
5. 商品统计：`GET /api/secondhand/statistics`
6. 更新状态：`PATCH /api/secondhand/products/{productId}/status`
7. 删除商品：`DELETE /api/secondhand/products/{productId}`

## 7. 学习资源与上传说明

当前学习资源模块已预留 Google Drive 接口，包含：

1. 资源列表
2. 下载链接生成
3. 上传会话创建（管理员后台可操作）

注意：当前是预留实现（Stub），返回标准化结构，后续可无缝替换为真实 Google Drive API 调用。

## 7.5 代刷课模块当前状态

当前代刷课模块已接入 Java 后端，支持课程配置、积分扣费下单与脚本回写预留：

1. 前台下单页：`pages/course-brush.html`
2. 公开课程列表：`GET /api/course-brush/courses`
3. 用户提交订单：`POST /api/course-brush/submit`
4. 我的订单：`GET /api/course-brush/my-orders`
5. 管理员课程列表：`GET /api/course-brush/admin/courses`
6. 管理员保存课程：`POST /api/course-brush/admin/courses`
7. 管理员启停课程：`POST /api/course-brush/admin/courses/{courseId}/status`
8. 管理员订单列表：`GET /api/course-brush/admin/orders`
9. 管理员更新订单状态：`POST /api/course-brush/admin/orders/{orderId}/status`
10. 自动化脚本说明：`GET /api/course-brush/automation/spec`
11. 自动化脚本回写：`POST /api/course-brush/automation/submit`

## 8. 关键接口（节选）

1. 用户/管理员登录：`POST /api/auth/login`
2. 公开代课任务列表：`GET /api/substitute/tasks`
3. 发布代课任务：`POST /api/substitute/publish`
4. 管理员代课全量任务：`GET /api/substitute/admin/tasks`
5. 管理员代课强制改状态：`POST /api/substitute/admin/tasks/{taskId}/status`
6. 管理员调整积分：`POST /api/points/admin/adjust`
7. 管理员启用/禁用用户：`POST /api/points/admin/users/{userId}/status`
8. 管理员重置签到：`POST /api/points/admin/users/{userId}/reset-signin`
9. 管理员积分流水：`GET /api/points/admin/history`
10. 管理员上传资料：`POST /api/resources/admin/materials/upload`
11. 管理员上传软件：`POST /api/resources/admin/software/upload`
12. 代刷课下单：`POST /api/course-brush/submit`
13. 管理员保存刷课课程：`POST /api/course-brush/admin/courses`

完整接口请查看：[docs/API-DOCUMENTATION.md](/e:/project/docs/API-DOCUMENTATION.md)

## 9. 部署说明

生产部署请直接参考：

[BAOTA-DEPLOYMENT-GUIDE.md](/e:/project/BAOTA-DEPLOYMENT-GUIDE.md)

该文档已按当前项目结构（`pages/` + `backend-java/`）更新。
