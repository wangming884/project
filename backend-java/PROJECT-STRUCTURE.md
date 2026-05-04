# 项目结构文档

## 📁 完整目录结构

```
backend-java/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── campus/
│       │           ├── CampusPlatformApplication.java    # 应用启动类
│       │           │
│       │           ├── common/                           # 通用类
│       │           │   └── Result.java                   # 统一响应结果
│       │           │
│       │           ├── config/                           # 配置类
│       │           │   ├── MyBatisPlusConfig.java        # MyBatis Plus 配置
│       │           │   └── SecurityConfig.java           # Spring Security 配置
│       │           │
│       │           ├── controller/                       # 控制器层
│       │           │   ├── AuthController.java           # 认证控制器
│       │           │   ├── PointsController.java         # 积分控制器
│       │           │   ├── CheckinController.java        # 签到控制器
│       │           │   ├── SecondhandController.java     # 二手交易控制器
│       │           │   └── SubstituteController.java     # 代课平台控制器
│       │           │
│       │           ├── dao/                              # 数据访问层（新增）
│       │           │   ├── BaseDao.java                  # DAO 基础接口
│       │           │   └── UserDao.java                  # 用户 DAO
│       │           │
│       │           ├── entity/                           # 实体类
│       │           │   ├── User.java                     # 用户实体
│       │           │   ├── PointsHistory.java            # 积分历史实体
│       │           │   ├── CheckinRecord.java            # 签到记录实体
│       │           │   ├── SecondhandProduct.java        # 二手商品实体
│       │           │   └── SubstituteTask.java           # 代课任务实体
│       │           │
│       │           ├── exception/                        # 异常处理
│       │           │   └── GlobalExceptionHandler.java   # 全局异常处理器
│       │           │
│       │           ├── filter/                           # 过滤器
│       │           │   └── JwtAuthenticationFilter.java  # JWT 认证过滤器
│       │           │
│       │           ├── mapper/                           # Mapper 接口
│       │           │   ├── UserMapper.java               # 用户 Mapper
│       │           │   ├── PointsHistoryMapper.java      # 积分历史 Mapper
│       │           │   ├── CheckinRecordMapper.java      # 签到记录 Mapper
│       │           │   ├── SecondhandProductMapper.java  # 二手商品 Mapper
│       │           │   └── SubstituteTaskMapper.java     # 代课任务 Mapper
│       │           │
│       │           ├── service/                          # 服务层
│       │           │   ├── AuthService.java              # 认证服务
│       │           │   ├── PointsService.java            # 积分服务
│       │           │   ├── CheckinService.java           # 签到服务
│       │           │   ├── SecondhandService.java        # 二手交易服务
│       │           │   └── SubstituteService.java        # 代课平台服务
│       │           │
│       │           └── util/                             # 工具类
│       │               └── JwtUtil.java                  # JWT 工具类
│       │
│       └── resources/
│           ├── application.yml                           # 应用配置文件
│           └── mapper/                                   # MyBatis XML（可选）
│
├── sql/
│   └── schema.sql                                        # 数据库初始化脚本
│
├── .gitignore                                            # Git 忽略配置
├── pom.xml                                               # Maven 配置文件
├── start.sh                                              # Linux/Mac 启动脚本
├── start.bat                                             # Windows 启动脚本
│
├── README.md                                             # 项目说明文档
├── JAVA-BACKEND-GUIDE.md                                 # 开发指南
├── DATABASE-INTERFACE.md                                 # 数据库接口文档
└── PROJECT-STRUCTURE.md                                  # 本文件
```

---

## 📦 模块说明

### 1. 启动类（CampusPlatformApplication.java）

应用程序的入口点，负责启动 Spring Boot 应用。

```java
@SpringBootApplication
public class CampusPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusPlatformApplication.class, args);
    }
}
```

---

### 2. 通用类（common/）

#### Result.java - 统一响应结果

封装所有 API 的响应格式，确保前后端接口统一。

```java
{
    "code": 200,
    "message": "success",
    "data": { ... }
}
```

---

### 3. 配置类（config/）

#### SecurityConfig.java - 安全配置

- JWT 认证配置
- CORS 跨域配置
- 白名单路径配置
- 密码加密配置

#### MyBatisPlusConfig.java - MyBatis Plus 配置

- 分页插件配置
- 自动填充配置
- 逻辑删除配置

---

### 4. 控制器层（controller/）

处理 HTTP 请求，调用 Service 层业务逻辑。

| 控制器 | 路径前缀 | 功能 |
|--------|----------|------|
| AuthController | `/auth` | 用户认证（注册、登录、登出） |
| PointsController | `/points` | 积分管理（签到、兑换、历史） |
| CheckinController | `/checkin` | 晚寝签到（提交、查询、审核） |
| SecondhandController | `/secondhand` | 二手交易（发布、浏览、管理） |
| SubstituteController | `/substitute` | 代课平台（发布、接单、完成） |

---

### 5. 数据访问层（dao/）⭐ 新增

#### BaseDao.java - DAO 基础接口

提供通用的数据库操作方法：

- `findById()` - 根据ID查询
- `findAll()` - 查询所有
- `findByCondition()` - 条件查询
- `findPage()` - 分页查询
- `insert()` - 插入
- `updateById()` - 更新
- `deleteById()` - 删除
- `count()` - 统计
- `exists()` - 判断存在

#### UserDao.java - 用户 DAO

扩展 BaseDao，提供用户相关的数据访问方法：

- `findByUsername()` - 根据用户名查询
- `findByEmail()` - 根据邮箱查询
- `existsByUsername()` - 检查用户名是否存在
- `updatePoints()` - 更新用户积分

**优势**：
- 解耦业务逻辑和数据访问
- 统一数据访问接口
- 便于单元测试
- 易于扩展（缓存、日志等）

---

### 6. 实体类（entity/）

对应数据库表的 Java 类，使用 Lombok 简化代码。

| 实体类 | 数据库表 | 说明 |
|--------|----------|------|
| User | users | 用户信息 |
| PointsHistory | points_history | 积分变动历史 |
| CheckinRecord | checkin_records | 晚寝签到记录 |
| SecondhandProduct | secondhand_products | 二手商品信息 |
| SubstituteTask | substitute_tasks | 代课任务信息 |

**注解说明**：
- `@Data` - 自动生成 getter/setter
- `@TableName` - 指定数据库表名
- `@TableId` - 指定主键
- `@TableField` - 指定字段映射
- `@TableLogic` - 逻辑删除标记

---

### 7. 异常处理（exception/）

#### GlobalExceptionHandler.java - 全局异常处理器

统一处理所有异常，返回友好的错误信息。

```java
@ExceptionHandler(RuntimeException.class)
public Result<Void> handleRuntimeException(RuntimeException e) {
    return Result.error(e.getMessage());
}
```

---

### 8. 过滤器（filter/）

#### JwtAuthenticationFilter.java - JWT 认证过滤器

拦截所有请求，验证 JWT Token 的有效性。

**流程**：
1. 从 Header 获取 Token
2. 验证 Token 有效性
3. 提取用户ID
4. 设置到 Security 上下文
5. 放行请求

---

### 9. Mapper 接口（mapper/）

继承 MyBatis Plus 的 `BaseMapper`，提供基础的 CRUD 操作。

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper 后自动拥有以下方法：
    // - selectById()
    // - selectList()
    // - insert()
    // - updateById()
    // - deleteById()
    // 等等...
}
```

---

### 10. 服务层（service/）

业务逻辑的核心，处理复杂的业务规则。

| 服务类 | 功能 |
|--------|------|
| AuthService | 用户注册、登录、获取用户信息 |
| PointsService | 积分管理、签到、兑换、历史记录 |
| CheckinService | 晚寝签到、记录查询、审核管理 |
| SecondhandService | 商品发布、浏览、更新、删除 |
| SubstituteService | 任务发布、接单、完成、取消 |

**特点**：
- 使用 `@Transactional` 保证事务一致性
- 封装复杂的业务逻辑
- 调用 DAO 层访问数据库

---

### 11. 工具类（util/）

#### JwtUtil.java - JWT 工具类

提供 JWT Token 的生成和验证功能。

```java
// 生成 Token
String token = jwtUtil.generateToken(userId);

// 验证 Token
boolean valid = jwtUtil.validateToken(token);

// 提取用户ID
Long userId = jwtUtil.getUserIdFromToken(token);
```

---

## 🔄 请求处理流程

### 完整流程图

```
客户端请求
    ↓
JwtAuthenticationFilter（JWT 验证）
    ↓
Controller 层（接收请求）
    ↓
Service 层（业务逻辑）
    ↓
DAO 层（数据访问抽象）
    ↓
Mapper 层（MyBatis Plus）
    ↓
Database（数据库）
    ↓
返回结果（Result 封装）
    ↓
客户端接收
```

### 示例：用户登录流程

1. **客户端** 发送 POST 请求到 `/api/auth/login`
2. **JwtAuthenticationFilter** 检查是否为白名单路径，放行
3. **AuthController** 接收请求，提取用户名和密码
4. **AuthService** 验证用户名和密码
5. **UserDao** 查询用户信息
6. **UserMapper** 执行数据库查询
7. **AuthService** 生成 JWT Token
8. **AuthController** 返回 Token 和用户信息
9. **客户端** 保存 Token，后续请求携带

---

## 🎯 设计模式

### 1. 分层架构模式

- **Controller 层**：处理 HTTP 请求
- **Service 层**：业务逻辑
- **DAO 层**：数据访问抽象
- **Mapper 层**：数据库操作

### 2. 依赖注入模式

使用 Spring 的依赖注入，降低耦合度。

```java
@Service
public class UserService {
    private final UserDao userDao;
    
    // 构造器注入
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
}
```

### 3. 单例模式

Spring Bean 默认为单例模式，节省资源。

### 4. 模板方法模式

BaseDao 定义通用方法，具体 DAO 实现特定逻辑。

### 5. 策略模式

不同的 Service 实现不同的业务策略。

---

## 📊 数据库设计

### ER 图

```
users (用户表)
  ├── 1:N → points_history (积分历史)
  ├── 1:N → checkin_records (签到记录)
  ├── 1:N → secondhand_products (二手商品)
  └── 1:N → substitute_tasks (代课任务)
```

### 表关系

- **users** 是核心表
- 其他表通过 `user_id` 外键关联
- 使用逻辑删除（`deleted` 字段）
- 自动填充时间戳（`created_at`, `updated_at`）

---

## 🔐 安全机制

### 1. JWT 认证

- 无状态认证
- Token 有效期 7 天
- 使用 HS256 算法

### 2. 密码加密

- BCrypt 算法
- 每次加密结果不同（加盐）
- 单向加密，无法解密

### 3. CORS 配置

- 允许跨域请求
- 支持 Credentials
- 配置允许的请求方法和头部

### 4. 白名单机制

- 登录、注册接口无需认证
- 其他接口需要 JWT Token

---

## 📝 开发规范

### 1. 命名规范

- **类名**：大驼峰（PascalCase）
- **方法名**：小驼峰（camelCase）
- **常量**：全大写，下划线分隔
- **包名**：全小写

### 2. 注释规范

- 类注释：说明类的功能和作者
- 方法注释：说明参数、返回值、功能
- 复杂逻辑：添加行内注释

### 3. 异常处理

- 使用 `@Transactional` 保证事务
- 抛出 `RuntimeException` 触发回滚
- 全局异常处理器统一处理

### 4. 返回值规范

- 统一使用 `Result<T>` 包装
- 成功：`Result.success(data)`
- 失败：`Result.error(message)`

---

## 🚀 扩展指南

### 添加新功能模块

1. **创建实体类** (`entity/`)
2. **创建 Mapper 接口** (`mapper/`)
3. **创建 DAO 类** (`dao/`)
4. **创建 Service 服务** (`service/`)
5. **创建 Controller 控制器** (`controller/`)
6. **更新数据库脚本** (`sql/schema.sql`)

### 添加新接口

1. 在对应的 Controller 中添加方法
2. 使用 `@GetMapping`、`@PostMapping` 等注解
3. 调用 Service 层方法
4. 返回 `Result<T>` 对象

### 添加新的数据访问方法

1. 在对应的 DAO 中添加方法
2. 使用 `LambdaQueryWrapper` 构建查询条件
3. 调用 BaseDao 的通用方法

---

## 📚 相关文档

- 📖 [README.md](README.md) - 项目说明
- 📚 [JAVA-BACKEND-GUIDE.md](JAVA-BACKEND-GUIDE.md) - 开发指南
- 🗄️ [DATABASE-INTERFACE.md](DATABASE-INTERFACE.md) - 数据库接口文档
- 📋 [../API-DOCUMENTATION.md](../API-DOCUMENTATION.md) - API 接口文档

---

## 🎉 总结

本项目采用了**企业级的分层架构设计**，具有以下特点：

✅ **清晰的分层结构** - Controller → Service → DAO → Mapper → Database  
✅ **完善的安全机制** - JWT 认证 + BCrypt 加密  
✅ **统一的响应格式** - Result 封装  
✅ **规范的代码风格** - 遵循阿里巴巴 Java 开发手册  
✅ **易于扩展维护** - 模块化设计，低耦合高内聚  
✅ **完整的文档支持** - 详细的开发文档和注释  

**祝开发顺利！🎉**
