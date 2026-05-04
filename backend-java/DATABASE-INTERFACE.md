# 数据库接口文档

## 📚 概述

本文档描述了校园综合服务平台的数据库接口设计，包括数据访问层（DAO）的架构和使用方法。

---

## 🏗️ 架构设计

### 三层架构

```
Controller 层（控制器）
    ↓
Service 层（业务逻辑）
    ↓
DAO 层（数据访问）← 新增抽象层
    ↓
Mapper 层（MyBatis Plus）
    ↓
Database（数据库）
```

### 为什么需要 DAO 层？

1. **解耦业务逻辑和数据访问**
   - Service 层不直接依赖 Mapper
   - 便于切换不同的数据访问实现

2. **统一数据访问接口**
   - 提供通用的 CRUD 操作
   - 减少重复代码

3. **便于测试**
   - 可以轻松 Mock DAO 层
   - 不依赖真实数据库

4. **扩展性强**
   - 可以添加缓存层
   - 可以添加数据验证
   - 可以添加审计日志

---

## 📦 DAO 层结构

### BaseDao 接口

所有 DAO 的基础接口，提供通用的数据库操作方法。

```java
public interface BaseDao<T, M extends BaseMapper<T>> {
    // 基础查询
    T findById(Long id);
    List<T> findAll();
    T findOneByCondition(LambdaQueryWrapper<T> wrapper);
    List<T> findByCondition(LambdaQueryWrapper<T> wrapper);
    
    // 分页查询
    Page<T> findPage(Page<T> page, LambdaQueryWrapper<T> wrapper);
    
    // 统计
    Long count(LambdaQueryWrapper<T> wrapper);
    boolean exists(LambdaQueryWrapper<T> wrapper);
    
    // 增删改
    int insert(T entity);
    int updateById(T entity);
    int deleteById(Long id);
    int deleteByCondition(LambdaQueryWrapper<T> wrapper);
    
    // 批量操作
    int batchInsert(List<T> entities);
}
```

### 具体 DAO 实现

每个实体都有对应的 DAO 实现类：

- **UserDao** - 用户数据访问
- **PointsHistoryDao** - 积分历史数据访问
- **CheckinRecordDao** - 签到记录数据访问
- **SecondhandProductDao** - 二手商品数据访问
- **SubstituteTaskDao** - 代课任务数据访问

---

## 🔧 使用示例

### 1. 基础查询

```java
@Service
public class UserService {
    
    private final UserDao userDao;
    
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
    
    // 根据ID查询
    public User getUserById(Long id) {
        return userDao.findById(id);
    }
    
    // 查询所有用户
    public List<User> getAllUsers() {
        return userDao.findAll();
    }
    
    // 根据用户名查询
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
```

### 2. 条件查询

```java
// 查询积分大于100的用户
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.gt(User::getPoints, 100)
       .eq(User::getStatus, 1)
       .orderByDesc(User::getPoints);

List<User> users = userDao.findByCondition(wrapper);
```

### 3. 分页查询

```java
// 分页查询用户
Page<User> page = new Page<>(1, 10); // 第1页，每页10条
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getStatus, 1);

Page<User> result = userDao.findPage(page, wrapper);
```

### 4. 统计查询

```java
// 统计活跃用户数量
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getStatus, 1);

Long count = userDao.count(wrapper);

// 检查用户名是否存在
boolean exists = userDao.existsByUsername("demo");
```

### 5. 插入数据

```java
// 插入单个用户
User user = new User();
user.setUsername("newuser");
user.setEmail("newuser@example.com");
user.setPassword("encrypted_password");

userDao.insert(user);

// 批量插入
List<User> users = Arrays.asList(user1, user2, user3);
userDao.batchInsert(users);
```

### 6. 更新数据

```java
// 更新用户信息
User user = userDao.findById(1L);
user.setPoints(user.getPoints() + 10);
userDao.updateById(user);
```

### 7. 删除数据

```java
// 根据ID删除
userDao.deleteById(1L);

// 根据条件删除
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getStatus, 0);
userDao.deleteByCondition(wrapper);
```

---

## 🎯 自定义 DAO 方法

### UserDao 示例

```java
@Repository
public class UserDao implements BaseDao<User, UserMapper> {
    
    private final UserMapper userMapper;
    
    public UserDao(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    @Override
    public UserMapper getMapper() {
        return userMapper;
    }
    
    /**
     * 根据用户名查询
     */
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return findOneByCondition(wrapper);
    }
    
    /**
     * 根据邮箱查询
     */
    public User findByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return findOneByCondition(wrapper);
    }
    
    /**
     * 根据用户名或邮箱查询
     */
    public User findByUsernameOrEmail(String usernameOrEmail) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, usernameOrEmail)
               .or()
               .eq(User::getEmail, usernameOrEmail);
        return findOneByCondition(wrapper);
    }
    
    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return exists(wrapper);
    }
    
    /**
     * 更新用户积分
     */
    public int updatePoints(Long userId, int points) {
        User user = findById(userId);
        if (user != null) {
            user.setPoints(points);
            return updateById(user);
        }
        return 0;
    }
}
```

---

## 📊 数据库表结构

### 1. 用户表（users）

| 字段 | 类型 | 说明 | 索引 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY |
| username | VARCHAR(50) | 用户名 | UNIQUE, INDEX |
| email | VARCHAR(100) | 邮箱 | UNIQUE, INDEX |
| password | VARCHAR(255) | 密码（加密） | - |
| avatar | VARCHAR(500) | 头像URL | - |
| points | INT | 积分余额 | - |
| last_sign_in_date | VARCHAR(20) | 最后签到日期 | - |
| continuous_days | INT | 连续签到天数 | - |
| status | TINYINT | 账号状态 | INDEX |
| deleted | TINYINT | 逻辑删除 | - |
| created_at | DATETIME | 创建时间 | - |
| updated_at | DATETIME | 更新时间 | - |

### 2. 积分历史表（points_history）

| 字段 | 类型 | 说明 | 索引 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY |
| user_id | BIGINT | 用户ID | INDEX, FK |
| type | VARCHAR(20) | 类型 | INDEX |
| amount | INT | 变动数量 | - |
| balance | INT | 变动后余额 | - |
| description | VARCHAR(255) | 描述 | - |
| created_at | DATETIME | 创建时间 | INDEX |

### 3. 晚寝签到记录表（checkin_records）

| 字段 | 类型 | 说明 | 索引 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY |
| user_id | BIGINT | 用户ID | INDEX, FK |
| username | VARCHAR(50) | 用户名 | - |
| location | VARCHAR(200) | 签到位置 | - |
| checkin_time | DATETIME | 签到时间 | INDEX |
| status | VARCHAR(20) | 状态 | INDEX |
| remark | VARCHAR(500) | 备注 | - |
| review_remark | VARCHAR(500) | 审核备注 | - |
| latitude | DECIMAL(10,8) | 纬度 | - |
| longitude | DECIMAL(11,8) | 经度 | - |
| created_at | DATETIME | 创建时间 | INDEX |

### 4. 二手商品表（secondhand_products）

| 字段 | 类型 | 说明 | 索引 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY |
| title | VARCHAR(200) | 商品标题 | - |
| price | DECIMAL(10,2) | 价格 | - |
| category | VARCHAR(50) | 分类 | INDEX |
| description | TEXT | 描述 | - |
| images | TEXT | 图片URL | - |
| seller_id | BIGINT | 卖家ID | INDEX, FK |
| seller_name | VARCHAR(50) | 卖家姓名 | - |
| contact | VARCHAR(100) | 联系方式 | - |
| status | VARCHAR(20) | 状态 | INDEX |
| view_count | INT | 浏览次数 | - |
| deleted | TINYINT | 逻辑删除 | - |
| created_at | DATETIME | 创建时间 | INDEX |
| updated_at | DATETIME | 更新时间 | - |

### 5. 代课任务表（substitute_tasks）

| 字段 | 类型 | 说明 | 索引 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY |
| title | VARCHAR(200) | 任务标题 | - |
| course | VARCHAR(100) | 课程名称 | - |
| time | DATETIME | 上课时间 | INDEX |
| location | VARCHAR(100) | 上课地点 | - |
| reward | VARCHAR(100) | 酬金 | - |
| description | TEXT | 任务描述 | - |
| publisher_id | BIGINT | 发布者ID | INDEX, FK |
| publisher_name | VARCHAR(50) | 发布者姓名 | - |
| accepter_id | BIGINT | 接单者ID | INDEX |
| accepter_name | VARCHAR(50) | 接单者姓名 | - |
| status | VARCHAR(20) | 状态 | INDEX |
| deleted | TINYINT | 逻辑删除 | - |
| created_at | DATETIME | 创建时间 | - |
| updated_at | DATETIME | 更新时间 | - |

---

## 🔐 数据库连接配置

### application.yml

```yaml
spring:
  datasource:
    # 数据库连接URL
    url: jdbc:mysql://localhost:3306/campus_platform?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    
    # 数据库用户名
    username: root
    
    # 数据库密码
    password: your_password
    
    # 驱动类名
    driver-class-name: com.mysql.cj.jdbc.Driver
    
    # 连接池配置（HikariCP）
    hikari:
      # 最小空闲连接数
      minimum-idle: 5
      
      # 最大连接池大小
      maximum-pool-size: 20
      
      # 连接超时时间（毫秒）
      connection-timeout: 30000
      
      # 空闲连接超时时间（毫秒）
      idle-timeout: 600000
      
      # 连接最大生命周期（毫秒）
      max-lifetime: 1800000

# MyBatis Plus 配置
mybatis-plus:
  # Mapper XML 文件位置
  mapper-locations: classpath*:/mapper/**/*.xml
  
  # 实体类包路径
  type-aliases-package: com.campus.entity
  
  # 全局配置
  global-config:
    db-config:
      # 主键类型（AUTO 自增）
      id-type: auto
      
      # 逻辑删除字段
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
      
  # 配置
  configuration:
    # 驼峰命名转换
    map-underscore-to-camel-case: true
    
    # 日志输出
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

---

## 🚀 最佳实践

### 1. 使用 DAO 层而不是直接使用 Mapper

❌ **不推荐**:
```java
@Service
public class UserService {
    private final UserMapper userMapper;
    
    public User getUser(Long id) {
        return userMapper.selectById(id);
    }
}
```

✅ **推荐**:
```java
@Service
public class UserService {
    private final UserDao userDao;
    
    public User getUser(Long id) {
        return userDao.findById(id);
    }
}
```

### 2. 在 DAO 层封装复杂查询

```java
@Repository
public class UserDao implements BaseDao<User, UserMapper> {
    
    /**
     * 查询活跃用户（积分>0且状态正常）
     */
    public List<User> findActiveUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(User::getPoints, 0)
               .eq(User::getStatus, 1)
               .orderByDesc(User::getPoints);
        return findByCondition(wrapper);
    }
}
```

### 3. 使用事务

```java
@Service
public class PointsService {
    
    private final UserDao userDao;
    private final PointsHistoryDao pointsHistoryDao;
    
    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long userId, int amount) {
        // 更新用户积分
        User user = userDao.findById(userId);
        user.setPoints(user.getPoints() + amount);
        userDao.updateById(user);
        
        // 记录积分历史
        PointsHistory history = new PointsHistory();
        history.setUserId(userId);
        history.setAmount(amount);
        history.setBalance(user.getPoints());
        pointsHistoryDao.insert(history);
    }
}
```

### 4. 添加缓存（可选）

```java
@Repository
public class UserDao implements BaseDao<User, UserMapper> {
    
    @Cacheable(value = "user", key = "#id")
    public User findById(Long id) {
        return getMapper().selectById(id);
    }
    
    @CacheEvict(value = "user", key = "#entity.id")
    public int updateById(User entity) {
        return getMapper().updateById(entity);
    }
}
```

---

## 📝 扩展 DAO 层

### 创建新的 DAO

1. **创建实体类** (`entity/`)
2. **创建 Mapper 接口** (`mapper/`)
3. **创建 DAO 类** (`dao/`)

示例：

```java
@Repository
public class ProductDao implements BaseDao<Product, ProductMapper> {
    
    private final ProductMapper productMapper;
    
    public ProductDao(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }
    
    @Override
    public ProductMapper getMapper() {
        return productMapper;
    }
    
    // 自定义方法
    public List<Product> findByCategory(String category) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCategory, category);
        return findByCondition(wrapper);
    }
}
```

---

## 🐛 常见问题

### Q1: DAO 层和 Mapper 层有什么区别？

**Mapper 层**:
- MyBatis Plus 提供的接口
- 直接操作数据库
- 提供基础的 CRUD 方法

**DAO 层**:
- 业务相关的数据访问接口
- 封装 Mapper 层
- 提供更高级的查询方法
- 便于测试和维护

### Q2: 什么时候使用 DAO，什么时候直接使用 Mapper？

**使用 DAO**:
- Service 层需要访问数据库
- 需要复杂的查询逻辑
- 需要添加缓存或日志

**直接使用 Mapper**:
- 简单的 CRUD 操作
- 临时测试代码

### Q3: 如何添加自定义 SQL？

在 `src/main/resources/mapper/` 目录下创建 XML 文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.campus.mapper.UserMapper">
    
    <select id="findActiveUsers" resultType="com.campus.entity.User">
        SELECT * FROM users 
        WHERE status = 1 AND points > 0
        ORDER BY points DESC
    </select>
    
</mapper>
```

---

## 📞 技术支持

- 📖 查看 `README.md` 了解项目概况
- 📚 查看 `JAVA-BACKEND-GUIDE.md` 了解开发指南
- 📋 查看 `API-DOCUMENTATION.md` 了解接口文档

---

**祝开发顺利！🎉**
