# Java 后端开发指南

## 📚 项目概述

这是一个基于 **Spring Boot 3.2** 的企业级后端实现，采用主流的技术栈和最佳实践。

## 🎯 技术选型

### 核心框架
- **Spring Boot 3.2.0** - 最新稳定版本
- **Spring Security** - 安全认证框架
- **MyBatis Plus 3.5.5** - 增强的 ORM 框架

### 数据存储
- **MySQL 8.0** - 关系型数据库
- **Redis** - 缓存和会话存储

### 安全认证
- **JWT (JSON Web Token)** - 无状态认证
- **BCrypt** - 密码加密算法

### 工具库
- **Hutool** - Java 工具类库
- **Lombok** - 简化 Java 代码

## 🏗️ 架构设计

### 分层架构

```
Controller 层 (控制器)
    ↓
Service 层 (业务逻辑)
    ↓
Mapper 层 (数据访问)
    ↓
Database (数据库)
```

### 目录结构

```
com.campus/
├── common/          # 通用类（Result、常量等）
├── config/          # 配置类（Security、MyBatis Plus等）
├── controller/      # 控制器（处理HTTP请求）
├── entity/          # 实体类（对应数据库表）
├── exception/       # 异常处理
├── filter/          # 过滤器（JWT认证等）
├── mapper/          # Mapper接口（数据访问）
├── service/         # 服务层（业务逻辑）
└── util/            # 工具类（JWT、加密等）
```

## 🔐 安全机制

### JWT 认证流程

1. **用户登录** → 验证用户名密码
2. **生成 Token** → 使用 JWT 生成 Token
3. **返回 Token** → 前端保存 Token
4. **携带 Token** → 后续请求携带 Token
5. **验证 Token** → 过滤器验证 Token
6. **放行请求** → 验证通过后处理请求

### 密码加密

使用 **BCrypt** 算法：
- 每次加密结果不同（加盐）
- 单向加密，无法解密
- 验证时使用 `matches()` 方法

```java
// 加密
String encoded = passwordEncoder.encode("123456");

// 验证
boolean matches = passwordEncoder.matches("123456", encoded);
```

## 📝 开发规范

### 命名规范

**类名**: 大驼峰（PascalCase）
```java
public class UserService { }
```

**方法名**: 小驼峰（camelCase）
```java
public User getUserById(Long id) { }
```

**常量**: 全大写，下划线分隔
```java
public static final String API_PREFIX = "/api";
```

**包名**: 全小写
```java
package com.campus.service;
```

### 注释规范

**类注释**:
```java
/**
 * 用户服务
 * 
 * @author Campus Platform Team
 */
public class UserService { }
```

**方法注释**:
```java
/**
 * 根据ID获取用户
 * 
 * @param id 用户ID
 * @return 用户信息
 */
public User getUserById(Long id) { }
```

### 返回值规范

统一使用 `Result<T>` 包装：

```java
// 成功
return Result.success(data);

// 失败
return Result.error("错误信息");
```

## 🔧 核心功能实现

### 1. 用户注册

```java
@PostMapping("/register")
public Result<Map<String, Object>> register(@Validated @RequestBody RegisterRequest request) {
    // 1. 验证参数（@Validated 自动验证）
    // 2. 检查用户名/邮箱是否已存在
    // 3. 加密密码
    // 4. 保存用户
    // 5. 返回结果
}
```

### 2. 用户登录

```java
@PostMapping("/login")
public Result<Map<String, Object>> login(@Validated @RequestBody LoginRequest request) {
    // 1. 查找用户
    // 2. 验证密码
    // 3. 生成 JWT Token
    // 4. 返回 Token 和用户信息
}
```

### 3. JWT 认证

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(...) {
        // 1. 从 Header 获取 Token
        // 2. 验证 Token 有效性
        // 3. 提取用户ID
        // 4. 设置到 Security 上下文
        // 5. 放行请求
    }
}
```

### 4. 积分系统

```java
@Transactional
public Map<String, Object> dailySignIn(Long userId) {
    // 1. 检查今天是否已签到
    // 2. 计算连续签到天数
    // 3. 计算奖励积分
    // 4. 更新用户积分
    // 5. 记录积分历史
    // 6. 返回结果
}
```

## 🗄️ 数据库设计

### 用户表 (users)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名 |
| email | VARCHAR(100) | 邮箱 |
| password | VARCHAR(255) | 密码（加密） |
| points | INT | 积分余额 |
| last_sign_in_date | VARCHAR(20) | 最后签到日期 |
| continuous_days | INT | 连续签到天数 |
| status | TINYINT | 账号状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 积分历史表 (points_history)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| type | VARCHAR(20) | 类型 |
| amount | INT | 变动数量 |
| balance | INT | 变动后余额 |
| description | VARCHAR(255) | 描述 |
| created_at | DATETIME | 创建时间 |

## 🧪 测试指南

### 单元测试

```java
@SpringBootTest
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    void testRegister() {
        // 测试用户注册
    }
}
```

### 接口测试

使用 **Postman** 或 **curl**：

```bash
# 注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"123456"}'

# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

## 🚀 部署指南

### 开发环境

```bash
# 启动
mvn spring-boot:run

# 或使用脚本
./start.sh      # Linux/Mac
start.bat       # Windows
```

### 生产环境

```bash
# 1. 打包
mvn clean package -DskipTests

# 2. 运行
java -jar target/campus-platform-1.0.0.jar

# 3. 后台运行
nohup java -jar target/campus-platform-1.0.0.jar > app.log 2>&1 &
```

### Docker 部署

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/campus-platform-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# 构建镜像
docker build -t campus-platform .

# 运行容器
docker run -d -p 8080:8080 campus-platform
```

## 📊 性能优化

### 1. 数据库优化

- 添加索引
- 使用连接池
- 避免 N+1 查询
- 使用分页查询

### 2. 缓存优化

```java
@Cacheable(value = "user", key = "#id")
public User getUserById(Long id) {
    return userMapper.selectById(id);
}
```

### 3. 异步处理

```java
@Async
public void sendEmail(String to, String content) {
    // 异步发送邮件
}
```

## 🐛 常见问题

### Q1: 启动报错 "Failed to configure a DataSource"

**原因**: 数据库配置错误或数据库未启动

**解决**:
1. 检查 `application.yml` 中的数据库配置
2. 确认 MySQL 已启动
3. 确认数据库已创建

### Q2: JWT Token 验证失败

**原因**: Token 格式错误或已过期

**解决**:
1. 检查 Header 格式：`Authorization: Bearer {token}`
2. 确认 Token 未过期
3. 检查 JWT 密钥配置

### Q3: 跨域问题

**原因**: CORS 配置不正确

**解决**:
已在 `SecurityConfig` 中配置，如仍有问题：
```java
configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
```

### Q4: 密码验证失败

**原因**: BCrypt 加密后的密码每次不同

**解决**:
使用 `passwordEncoder.matches()` 验证，不要直接比较字符串

## 📚 学习资源

### 官方文档
- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [MyBatis Plus 文档](https://baomidou.com/)
- [Spring Security 文档](https://spring.io/projects/spring-security)

### 推荐书籍
- 《Spring Boot 实战》
- 《深入浅出 Spring Boot》
- 《Java 并发编程实战》

### 在线教程
- [Spring Boot 中文文档](https://springdoc.cn/)
- [菜鸟教程 - Spring Boot](https://www.runoob.com/spring-boot/spring-boot-tutorial.html)

## 🎓 最佳实践

### 1. 代码规范
- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 统一异常处理

### 2. 安全规范
- 密码加密存储
- 使用 JWT 认证
- 防止 SQL 注入
- 输入参数验证

### 3. 性能规范
- 使用连接池
- 添加缓存
- 异步处理
- 分页查询

### 4. 日志规范
- 使用 SLF4J + Logback
- 分级记录日志
- 敏感信息脱敏

## 📞 技术支持

- 📧 邮箱: admin@mybrand.com
- 📖 文档: 查看 `README.md` 和 `API-DOCUMENTATION.md`
- 🐛 问题: 提交 GitHub Issues

---

**祝开发顺利！🎉**
