# 校园综合服务平台 - 文档整合总览（排除 README 与宝塔文档）

> 生成日期：2026-05-04  
> 本文整合了根目录除 `README.md`、`BAOTA-DEPLOYMENT-GUIDE.md` 以外的全部 Markdown 文档。

## 1. 整合范围

已整合以下 16 份文档：

1. `优化完成报告.md`
2. `ADVANCED-OPTIMIZATION-GUIDE.md`
3. `API-DOCUMENTATION.md`
4. `BACKEND-COMPARISON.md`
5. `CHANGELOG.md`
6. `FINAL-OPTIMIZATION-SUMMARY.md`
7. `JAVA-BACKEND-SUMMARY.md`
8. `LATEST-OPTIMIZATION.md`
9. `OPTIMIZATION-CHECKLIST.md`
10. `OPTIMIZATION-COMPLETE.md`
11. `OPTIMIZATION-SUMMARY-V1.1.md`
12. `OPTIMIZATION-SUMMARY.md`
13. `PROJECT-OVERVIEW.md`
14. `QUICK-DEPLOYMENT.md`
15. `QUICK-REFERENCE.md`
16. `QUICK-START.md`

## 2. 项目一句话总结

这是一个「前端静态页面 + Java Spring Boot 后端 + MySQL + Redis」的校园综合服务平台，覆盖用户认证、积分体系、晚寝签到、二手交易、代课互助、学习资源等模块，并完成了 v1.1 级别的工程化优化。

## 3. 当前版本与状态

1. 当前主版本：`v1.1.x`（文档体系集中于 v1.1 优化成果）。
2. 前端：可直接静态运行，后端不可用时具备本地降级能力。
3. 后端：`backend-java/` 已具备生产配置、部署脚本、Docker 文件与数据库脚本。
4. 文档：内容完整但重复较多，本整合文档用于统一入口。

## 4. 功能模块总览

1. 用户认证：注册、登录、登出、鉴权、用户信息。
2. 积分系统：余额查询、每日签到、兑换码兑换、积分购买、积分历史。
3. 晚寝签到：提交签到、签到状态、签到记录。
4. 二手交易：列表、搜索、详情、发布、更新、删除、联系卖家。
5. 代课平台：任务列表、发布、接单、完成、取消。
6. 站长好物：推荐列表、详情、优惠券领取。
7. 学习资源：资料/软件列表、下载、上传（部分）。
8. 联系作者：表单提交。

## 5. 技术架构整合

### 5.1 前端

1. 技术：原生 HTML/CSS/JS。
2. API：`js/api-config.js` + `js/api-utils.js` 统一请求封装。
3. 生产地址策略：非 `localhost` 时自动走 `/api`。
4. 特点：支持后端异常时本地存储降级。

### 5.2 后端

1. 框架：Spring Boot 3.2 + MyBatis-Plus + Spring Security + JWT。
2. 数据：MySQL + Redis。
3. 规范：统一返回、全局异常、DTO、常量、枚举、拦截器、限流切面。
4. 文档：支持 Swagger/OpenAPI。

### 5.3 数据库

核心表共 5 张：

1. `users`
2. `points_history`
3. `checkin_records`
4. `secondhand_products`
5. `substitute_tasks`

## 6. API 文档要点整合

1. 基础路径：后端上下文路径为 `/api`。
2. 响应规范：统一 JSON 响应结构（success/message/data 等）。
3. 认证方式：Bearer Token（主）+ Cookie（可选）。
4. 错误处理：覆盖 401/403/404/500 及网络异常。

建议直接对照 `API-DOCUMENTATION.md` 查看每个接口的参数与示例。

## 7. 部署路径整合

### 7.1 推荐路径（新手）

1. 宝塔面板部署（详细见 `BAOTA-DEPLOYMENT-GUIDE.md` 重写版）。
2. 单域名部署时，前端无需改 `api-config.js`，只要把 `/api` 反代到后端即可。

### 7.2 其他路径

1. Docker 部署（已具备 Dockerfile / docker-compose）。
2. 传统命令行部署（Jar + 反向代理）。

## 8. 优化成果整合（v1.1 核心）

来自多份优化文档的交集结论如下：

1. 完善异常体系（业务异常、认证授权异常、资源不存在异常）。
2. 补全枚举与常量，减少硬编码。
3. 增加 DTO 层与参数验证。
4. 引入 Redis 缓存能力。
5. 增加请求日志拦截器。
6. 增加接口限流能力（注解 + AOP）。
7. 增加 Swagger 文档支持。
8. 完善部署脚本与生产配置。

## 9. Java 与 Node 后端选型结论

综合 `BACKEND-COMPARISON.md`：

1. 企业级长期项目优先 Java（稳健、规范、扩展性强）。
2. 快速迭代和轻量原型可选 Node.js（上手快、开发效率高）。
3. 当前仓库主推 Java 后端路线，文档与脚本更完整。

## 10. 快速启动整合版（本地）

1. 启动前端：直接打开 HTML 或用本地静态服务器。
2. 启动后端：进入 `backend-java/`，执行 `mvn spring-boot:run` 或打包后 `java -jar`。
3. 初始化数据库：执行 `backend-java/sql/schema.sql`。
4. 验证链路：登录、每日签到、晚寝签到、二手列表。

## 11. 常见问题整合

1. 前端连不上后端：检查 `/api` 反向代理或本地端口配置。
2. Token 无效：确认登录成功后本地是否写入 `authToken`。
3. Redis 报错：确认 Redis 端口/密码与 `application*.yml` 一致。
4. 数据库连接失败：检查 MySQL 用户、密码、库名与字符集参数。
5. 端口冲突：修改后端端口或释放占用。

## 12. 里程碑与变更记录整合

1. `v1.0`：基础业务模块成型（认证、积分、签到、二手、代课）。
2. `v1.1`：工程化与可维护性优化（异常、DTO、限流、缓存、文档）。
3. 后续规划：功能增强、性能优化、架构升级（依据 `CHANGELOG.md` 与多份总结文档）。

## 13. 文档归并建议

为避免重复维护，建议后续保留 4 层文档结构：

1. `README.md`：项目入口与最小启动。
2. `DOCS-INTEGRATED.md`：统一总览（本文）。
3. `BAOTA-DEPLOYMENT-GUIDE.md`：部署实操主文档。
4. `API-DOCUMENTATION.md`：接口标准主文档。

其余历史文档可归档到 `docs/archive/`，减少主目录噪音。
