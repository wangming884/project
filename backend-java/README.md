# 校园综合服务平台 - Java 后端

基于 Spring Boot 3.2 + MyBatis Plus 的企业级后端实现。

## 🚀 技术栈

- **Spring Boot 3.2.0** - 核心框架
- **Spring Security** - 安全认证
- **MyBatis Plus 3.5.5** - ORM 框架
- **MySQL 8.0** - 数据库
- **Redis** - 缓存
- **JWT** - Token 认证
- **Hutool** - 工具类库
- **Lombok** - 简化代码

## 📋 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+ (可选)

## 🔧 快速开始

### 1. 克隆项目

```bash
cd backend-java
```

### 2. 创建数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source sql/schema.sql
```

或者直接导入：
```bash
mysql -u root -p < sql/schema.sql
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_platform
    username: root
    password: your_password  # 修改为你的密码
```

### 4. 启动项目

```bash
# 使用 Maven
mvn spring-boot:run

# 或者先打包再运行
mvn clean package
java -jar target/campus-platform-1.0.0.jar
```

### 5. 访问接口

```
http://localhost:8080/api
```

## 📚 项目结构

```
backend-java/
├── src/main/java/com/campus/
│   ├── CampusPlatformApplication.java  # 启动类
│   ├── common/                         # 通用类
│   │   └── Result.java                 # 统一响应结果
│   ├── config/                         # 配置类
│   │   └── SecurityConfig.java         # Security 配置
│   ├── controller/                     # 控制器
│   │   ├── AuthController.java         # 认证控制器
│   │   └── PointsController.java       # 积分控制器
│   ├── entity/                         # 实体类
│   │   ├── User.java                   # 用户实体
│   │   ├── PointsHistory.java          # 积分历史
│   │   ├── CheckinRecord.java          # 签到记录
│   │   └── SecondhandProduct.java      # 二手商品
│   ├── filter/                         # 过滤器
│   │   └── JwtAuthenticationFilter.java # JWT 认证过滤器
│   ├── mapper/                         # Mapper 接口
│   │   ├── UserMapper.java
│   │   ├── PointsHistoryMapper.java
│   │   ├── CheckinRecordMapper.java
│   │   └── SecondhandProductMapper.java
│   ├── service/                        # 服务层
│   │   ├── AuthService.java            # 认证服务
│   │   └── PointsService.java          # 积分服务
│   └── util/                           # 工具类
│       └── JwtUtil.java                # JWT 工具
├── src/main/resources/
│   ├── application.yml                 # 配置文件
│   └── mapper/                         # MyBatis XML (可选)
├── sql/
│   └── schema.sql                      # 数据库初始化脚本
├── pom.xml                             # Maven 配置
└── README.md                           # 本文件
```

## 🔐 已实现的接口

### 认证模块

| 接口 | 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|------|
| 用户注册 | POST | `/auth/register` | 注册新用户 | ❌ |
| 用户登录 | POST | `/auth/login` | 用户登录 | ❌ |
| 获取用户信息 | GET | `/auth/user` | 获取当前用户信息 | ✅ |
| 检查登录状态 | GET | `/auth/check` | 检查是否已登录 | ✅ |
| 用户登出 | POST | `/auth/logout` | 用户登出 | ✅ |

### 积分模块

| 接口 | 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|------|
| 获取积分余额 | GET | `/points/balance` | 查询积分余额 | ✅ |
| 每日签到 | POST | `/points/sign-in` | 每日签到领积分 | ✅ |
| 兑换积分码 | POST | `/points/redeem` | 兑换积分码 | ✅ |
| 积分历史 | GET | `/points/history` | 查询积分历史 | ✅ |

## 🧪 测试接口

### 使用 curl

**注册用户**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "123456"
  }'
```

**登录**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo",
    "password": "123456"
  }'
```

**获取积分余额** (需要 Token):
```bash
curl -X GET http://localhost:8080/api/points/balance \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 使用 Postman

1. 导入接口文档（参考 `API-DOCUMENTATION.md`）
2. 先调用登录接口获取 Token
3. 在其他接口的 Header 中添加：
   ```
   Authorization: Bearer {token}
   ```

## 🗄️ 数据库设计

### 核心表

- **users** - 用户表
- **points_history** - 积分历史表
- **checkin_records** - 晚寝签到记录表
- **secondhand_products** - 二手商品表
- **substitute_tasks** - 代课任务表

详细设计见 `sql/schema.sql`

## 🔒 安全配置

### JWT Token

- **有效期**: 7天
- **算法**: HS256
- **Header**: `Authorization: Bearer {token}`

### 密码加密

使用 BCrypt 算法加密存储，安全强度高。

### CORS 配置

已配置跨域支持，允许前端访问。

## 📝 开发指南

### 添加新接口

1. **创建实体类** (`entity/`)
2. **创建 Mapper 接口** (`mapper/`)
3. **创建 Service 服务** (`service/`)
4. **创建 Controller 控制器** (`controller/`)

### 示例：添加新模块

```java
// 1. 实体类
@Data
@TableName("my_table")
public class MyEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
}

// 2. Mapper
@Mapper
public interface MyMapper extends BaseMapper<MyEntity> {
}

// 3. Service
@Service
public class MyService {
    private final MyMapper myMapper;
    
    public MyService(MyMapper myMapper) {
        this.myMapper = myMapper;
    }
    
    public MyEntity getById(Long id) {
        return myMapper.selectById(id);
    }
}

// 4. Controller
@RestController
@RequestMapping("/my")
public class MyController {
    private final MyService myService;
    
    public MyController(MyService myService) {
        this.myService = myService;
    }
    
    @GetMapping("/{id}")
    public Result<MyEntity> getById(@PathVariable Long id) {
        MyEntity entity = myService.getById(id);
        return Result.success(entity);
    }
}
```

## 🐛 常见问题

### Q1: 启动失败，提示数据库连接错误？

**解决方案**:
1. 检查 MySQL 是否启动
2. 确认数据库名称、用户名、密码是否正确
3. 确认已执行 `schema.sql` 初始化脚本

### Q2: Token 验证失败？

**解决方案**:
1. 检查 Token 是否正确携带在 Header 中
2. 确认 Token 格式：`Authorization: Bearer {token}`
3. 检查 Token 是否过期

### Q3: 跨域问题？

**解决方案**:
已在 `SecurityConfig` 中配置 CORS，如仍有问题：
1. 检查前端请求是否携带 `credentials: 'include'`
2. 确认 `allowedOriginPatterns` 配置

### Q4: 如何修改 JWT 密钥？

编辑 `application.yml`:
```yaml
jwt:
  secret: your-new-secret-key-must-be-at-least-256-bits
```

## 📦 打包部署

### 打包

```bash
mvn clean package -DskipTests
```

生成的 jar 文件位于 `target/campus-platform-1.0.0.jar`

### 运行

```bash
java -jar target/campus-platform-1.0.0.jar
```

### 指定配置文件

```bash
java -jar target/campus-platform-1.0.0.jar --spring.config.location=application-prod.yml
```

### 后台运行

```bash
nohup java -jar target/campus-platform-1.0.0.jar > app.log 2>&1 &
```

## 🔄 下一步开发

- [ ] 完成晚寝签到接口
- [ ] 完成二手交易接口
- [ ] 完成代课平台接口
- [ ] 添加文件上传功能
- [ ] 集成 Redis 缓存
- [ ] 添加接口文档（Swagger）
- [ ] 添加单元测试
- [ ] 添加日志记录
- [ ] 性能优化

## 📞 技术支持

- 📧 邮箱: admin@mybrand.com
- 📚 文档: 查看项目根目录的 `API-DOCUMENTATION.md`

## 📄 许可证

MIT License

---

**祝开发顺利！🎉**
