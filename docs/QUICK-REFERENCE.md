# 🚀 快速参考指南

## 📋 常用操作速查

### 1. 启动项目

```bash
# 进入后端目录
cd backend-java

# 方式1: Maven启动
mvn spring-boot:run

# 方式2: 打包后启动
mvn clean package
java -jar target/campus-platform-1.0.0.jar

# 方式3: 使用脚本
./start.sh      # Linux/Mac
start.bat       # Windows
```

### 2. 访问地址

```
前端页面:    直接打开 index.html
后端API:     http://localhost:8080/api
Swagger文档: http://localhost:8080/api/swagger-ui.html
API Docs:    http://localhost:8080/api/v3/api-docs
```

---

## 💡 代码示例速查

### 1. 使用自定义异常

```java
// 资源未找到
throw new ResourceNotFoundException("User", userId);

// 认证失败
throw new AuthenticationException("Invalid credentials");

// 授权失败
throw new AuthorizationException("Access denied");

// 业务异常
throw new BusinessException("Custom error message");
throw new BusinessException(ErrorCode.INSUFFICIENT_POINTS, "积分不足");
```

### 2. 使用枚举

```java
// 设置状态
product.setStatus(ProductStatus.AVAILABLE.getCode());
user.setStatus(UserStatus.NORMAL.getCode());

// 判断状态
if (ProductStatus.AVAILABLE.getCode().equals(product.getStatus())) {
    // 商品可售
}

// 从代码获取枚举
ProductStatus status = ProductStatus.fromCode("available");
```

### 3. 使用Redis缓存

```java
// 注入RedisUtil
@Autowired
private RedisUtil redisUtil;

// 设置缓存
redisUtil.set("key", value);
redisUtil.set("key", value, 30, TimeUnit.MINUTES);

// 获取缓存
Object value = redisUtil.get("key");
User user = (User) redisUtil.get(CacheConstants.USER_CACHE_PREFIX + userId);

// 删除缓存
redisUtil.delete("key");

// 判断存在
Boolean exists = redisUtil.hasKey("key");

// 计数器（用于限流）
Long count = redisUtil.increment("rate_limit:login:192.168.1.1");
```

### 4. 使用限流注解

```java
// 基于IP限流
@RateLimit(key = "login", time = 60, count = 5, limitType = RateLimit.LimitType.IP)
@PostMapping("/login")
public Result<String> login(@RequestBody LoginRequest request) {
    // 每个IP每分钟最多5次登录请求
}

// 基于用户限流
@RateLimit(key = "publish", time = 60, count = 10, limitType = RateLimit.LimitType.USER)
@PostMapping("/publish")
public Result<String> publish(@RequestBody ProductRequest request) {
    // 每个用户每分钟最多10次发布请求
}
```

### 5. 使用DTO和验证

```java
// Controller
@PostMapping("/register")
public Result<String> register(@Valid @RequestBody RegisterRequest request) {
    // 参数自动验证，验证失败自动返回400错误
    return authService.register(request);
}

// DTO定义
@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
}
```

### 6. 使用分页

```java
// Controller
@GetMapping("/list")
public Result<PageResponse<Product>> list(@Valid PageRequest pageRequest) {
    return Result.success(productService.getProductList(pageRequest));
}

// Service
public PageResponse<Product> getProductList(PageRequest pageRequest) {
    Page<Product> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
    Page<Product> result = productMapper.selectPage(page, null);
    return PageResponse.of(result.getRecords(), result.getTotal(), 
                          pageRequest.getPageNum(), pageRequest.getPageSize());
}

// 前端请求
GET /api/products/list?pageNum=1&pageSize=10&sortField=createTime&sortOrder=desc
```

### 7. 使用常量

```java
// API常量
int pageSize = ApiConstants.DEFAULT_PAGE_SIZE;
String tokenHeader = ApiConstants.HEADER_TOKEN;
int maxFileSize = ApiConstants.MAX_FILE_SIZE;

// 缓存常量
String userKey = CacheConstants.USER_CACHE_PREFIX + userId;
long expire = CacheConstants.USER_CACHE_EXPIRE;

// 错误码
throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
throw new BusinessException(ErrorCode.INSUFFICIENT_POINTS, "积分不足");
```

---

## 🔧 配置速查

### 1. 数据库配置

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_platform
    username: root
    password: your_password
```

### 2. Redis配置

```yaml
# application.yml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
```

### 3. JWT配置

```yaml
# application.yml
jwt:
  secret: your-secret-key-change-in-production
  expiration: 604800000  # 7天
  header: Authorization
  prefix: Bearer 
```

### 4. 日志配置

```yaml
# application.yml
logging:
  level:
    com.campus: debug
    org.springframework.web: info
```

---

## 📊 API接口速查

### 认证接口

```
POST   /api/auth/register      # 注册
POST   /api/auth/login         # 登录
GET    /api/auth/user          # 获取用户信息
GET    /api/auth/check         # 检查登录状态
POST   /api/auth/logout        # 登出
```

### 积分接口

```
GET    /api/points/balance     # 获取积分余额
POST   /api/points/sign-in     # 每日签到
POST   /api/points/redeem      # 兑换积分码
GET    /api/points/history     # 积分历史
```

### 签到接口

```
POST   /api/checkin/submit     # 提交签到
GET    /api/checkin/records    # 查询记录
GET    /api/checkin/today      # 今日状态
GET    /api/checkin/statistics # 签到统计
POST   /api/checkin/approve/{id} # 审核签到
GET    /api/checkin/pending    # 待审核列表
```

### 二手交易接口

```
POST   /api/secondhand/publish           # 发布商品
GET    /api/secondhand/products          # 商品列表
GET    /api/secondhand/products/{id}     # 商品详情
GET    /api/secondhand/my-products       # 我的发布
PUT    /api/secondhand/products/{id}     # 更新商品
PATCH  /api/secondhand/products/{id}/status # 更新状态
DELETE /api/secondhand/products/{id}     # 删除商品
GET    /api/secondhand/statistics        # 商品统计
```

### 代课平台接口

```
POST   /api/substitute/publish           # 发布任务
GET    /api/substitute/tasks             # 任务列表
GET    /api/substitute/tasks/{id}        # 任务详情
POST   /api/substitute/tasks/{id}/accept # 接单
POST   /api/substitute/tasks/{id}/cancel-accept # 取消接单
POST   /api/substitute/tasks/{id}/complete # 完成任务
POST   /api/substitute/tasks/{id}/cancel # 取消任务
GET    /api/substitute/my-published      # 我发布的
GET    /api/substitute/my-accepted       # 我接的
GET    /api/substitute/statistics        # 任务统计
```

---

## 🐛 常见问题速查

### 1. 数据库连接失败

```bash
# 检查MySQL是否启动
systemctl status mysql

# 检查数据库是否存在
mysql -u root -p
> SHOW DATABASES;
> USE campus_platform;

# 重新导入数据库
mysql -u root -p < backend-java/sql/schema.sql
```

### 2. Redis连接失败

```bash
# 检查Redis是否启动
systemctl status redis

# 启动Redis
systemctl start redis

# 测试连接
redis-cli ping
```

### 3. 端口被占用

```bash
# 查看端口占用
netstat -ano | grep 8080

# 杀死进程
kill -9 <PID>

# 或修改端口
# application.yml
server:
  port: 8081
```

### 4. Maven依赖下载失败

```bash
# 清理并重新下载
mvn clean install -U

# 或使用阿里云镜像
# settings.xml
<mirror>
  <id>aliyun</id>
  <url>https://maven.aliyun.com/repository/public</url>
  <mirrorOf>central</mirrorOf>
</mirror>
```

---

## 📚 文档导航速查

### 快速开始
- [README.md](README.md) - 项目说明
- [QUICK-START.md](QUICK-START.md) - 快速开始
- [QUICK-REFERENCE.md](QUICK-REFERENCE.md) - 本文件

### 开发文档
- [backend-java/JAVA-BACKEND-GUIDE.md](backend-java/JAVA-BACKEND-GUIDE.md) - 开发指南
- [backend-java/DATABASE-INTERFACE.md](backend-java/DATABASE-INTERFACE.md) - 数据库接口
- [backend-java/PROJECT-STRUCTURE.md](backend-java/PROJECT-STRUCTURE.md) - 项目结构

### API文档
- [API-DOCUMENTATION.md](API-DOCUMENTATION.md) - API接口文档
- Swagger UI - http://localhost:8080/api/swagger-ui.html

### 优化文档
- [LATEST-OPTIMIZATION.md](LATEST-OPTIMIZATION.md) - 最新优化速览
- [ADVANCED-OPTIMIZATION-GUIDE.md](ADVANCED-OPTIMIZATION-GUIDE.md) - 深度优化指南
- [OPTIMIZATION-SUMMARY-V1.1.md](OPTIMIZATION-SUMMARY-V1.1.md) - 优化总结
- [OPTIMIZATION-CHECKLIST.md](OPTIMIZATION-CHECKLIST.md) - 优化清单

---

## 🎯 开发建议

### 1. 代码规范

- ✅ 使用枚举代替魔法值
- ✅ 使用常量代替硬编码
- ✅ 使用DTO进行数据传输
- ✅ 使用自定义异常
- ✅ 添加详细注释

### 2. 性能优化

- ✅ 使用Redis缓存热点数据
- ✅ 使用分页查询大量数据
- ✅ 使用异步处理耗时操作
- ✅ 使用连接池管理数据库连接
- ✅ 使用索引优化查询

### 3. 安全建议

- ✅ 使用JWT进行认证
- ✅ 使用BCrypt加密密码
- ✅ 使用限流防止攻击
- ✅ 使用参数验证防止注入
- ✅ 使用HTTPS传输数据

### 4. 测试建议

- ✅ 编写单元测试
- ✅ 编写集成测试
- ✅ 使用Swagger测试接口
- ✅ 使用Postman测试接口
- ✅ 进行压力测试

---

**快速参考指南 - 让开发更高效！🚀**

