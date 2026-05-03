# 校园综合服务平台 - 后端 API 接口文档

## 📋 目录

- [接口规范](#接口规范)
- [认证授权](#认证授权)
- [用户认证模块](#用户认证模块)
- [积分系统模块](#积分系统模块)
- [晚寝签到模块](#晚寝签到模块)
- [二手物品交易模块](#二手物品交易模块)
- [代课平台模块](#代课平台模块)
- [站长好物模块](#站长好物模块)
- [学习资源模块](#学习资源模块)
- [联系作者模块](#联系作者模块)
- [错误码说明](#错误码说明)

---

## 接口规范

### 基础信息

- **Base URL**: `/api`
- **请求格式**: `application/json`
- **响应格式**: `application/json`
- **字符编码**: `UTF-8`

### 统一响应格式

```json
{
  "success": true,           // 请求是否成功
  "message": "操作成功",      // 提示信息
  "data": {},                // 响应数据
  "code": 200,               // 业务状态码
  "timestamp": 1234567890    // 时间戳
}
```

### HTTP 状态码

- `200` - 请求成功
- `400` - 请求参数错误
- `401` - 未授权（未登录或 token 失效）
- `403` - 禁止访问（无权限）
- `404` - 资源不存在
- `500` - 服务器内部错误

---

## 认证授权

### Token 认证

所有需要登录的接口都需要在请求头中携带 token：

```
Authorization: Bearer {token}
```

### Cookie 认证（可选）

支持通过 Cookie 携带 session 信息，前端需设置：

```javascript
credentials: 'include'
```

---

## 用户认证模块

### 1. 用户注册

**接口**: `POST /api/auth/register`

**请求参数**:

```json
{
  "username": "zhangsan",      // 用户名，3-20字符
  "email": "zhangsan@example.com",  // 邮箱
  "password": "123456"         // 密码，至少6位
}
```

**响应示例**:

```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "userId": 1001,
    "username": "zhangsan",
    "email": "zhangsan@example.com"
  }
}
```

---

### 2. 用户登录

**接口**: `POST /api/auth/login`

**请求参数**:

```json
{
  "username": "zhangsan",      // 用户名或邮箱
  "password": "123456"         // 密码
}
```

**响应示例**:

```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "userId": 1001,
      "username": "zhangsan",
      "email": "zhangsan@example.com",
      "avatar": "https://example.com/avatar.jpg",
      "points": 100
    }
  }
}
```

---

### 3. 用户登出

**接口**: `POST /api/auth/logout`

**请求头**: 需要 token

**响应示例**:

```json
{
  "success": true,
  "message": "登出成功"
}
```

---

### 4. 检查登录状态

**接口**: `GET /api/auth/check`

**请求头**: 需要 token

**响应示例**:

```json
{
  "success": true,
  "data": {
    "isLoggedIn": true,
    "userId": 1001
  }
}
```

---

### 5. 获取用户信息

**接口**: `GET /api/auth/user`

**请求头**: 需要 token

**响应示例**:

```json
{
  "success": true,
  "data": {
    "userId": 1001,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "avatar": "https://example.com/avatar.jpg",
    "points": 100,
    "createdAt": "2026-05-01T10:00:00Z"
  }
}
```

---

## 积分系统模块

### 1. 获取积分余额

**接口**: `GET /api/points/balance`

**请求头**: 需要 token

**响应示例**:

```json
{
  "success": true,
  "data": {
    "balance": 150,
    "lastSignInDate": "2026-05-04"
  }
}
```

---

### 2. 每日签到

**接口**: `POST /api/points/sign-in`

**请求头**: 需要 token

**响应示例**:

```json
{
  "success": true,
  "message": "签到成功",
  "data": {
    "earnedPoints": 1,
    "balance": 151,
    "continuousDays": 5
  }
}
```

---

### 3. 兑换积分码

**接口**: `POST /api/points/redeem`

**请求头**: 需要 token

**请求参数**:

```json
{
  "code": "ABCD1234"
}
```

**响应示例**:

```json
{
  "success": true,
  "message": "兑换成功",
  "data": {
    "points": 50,
    "balance": 201
  }
}
```

---

### 4. 积分历史记录

**接口**: `GET /api/points/history`

**请求头**: 需要 token

**查询参数**:
- `page`: 页码，默认 1
- `pageSize`: 每页数量，默认 20

**响应示例**:

```json
{
  "success": true,
  "data": {
    "list": [
      {
        "id": 1,
        "type": "sign_in",
        "amount": 1,
        "balance": 151,
        "description": "每日签到",
        "createdAt": "2026-05-04T08:00:00Z"
      },
      {
        "id": 2,
        "type": "checkin",
        "amount": -10,
        "balance": 150,
        "description": "晚寝签到",
        "createdAt": "2026-05-03T22:00:00Z"
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 20
  }
}
```

---

### 5. 购买积分

**接口**: `POST /api/points/purchase`

**请求头**: 需要 token

**请求参数**:

```json
{
  "amount": 100,
  "paymentMethod": "alipay"
}
```

**响应示例**:

```json
{
  "success": true,
  "message": "购买成功",
  "data": {
    "orderId": "ORDER123456",
    "amount": 100,
    "balance": 251
  }
}
```

---

## 晚寝签到模块

### 1. 提交晚寝签到

**接口**: `POST /api/checkin/submit`

**请求头**: 需要 token

**请求参数**:

```json
{
  "account": "2021001",
  "password": "123456",
  "dorm": "男生公寓 3 栋",
  "location": {
    "latitude": 39.9042,
    "longitude": 116.4074
  }
}
```

**响应示例**:

```json
{
  "success": true,
  "message": "签到成功",
  "data": {
    "checkInId": 1001,
    "remainingPoints": 140,
    "checkInTime": "2026-05-04T22:30:00Z"
  }
}
```

---

### 2. 获取签到状态

**接口**: `GET /api/checkin/status`

**请求头**: 需要 token

**响应示例**:

```json
{
  "success": true,
  "data": {
    "hasCheckedInToday": true,
    "lastCheckInTime": "2026-05-04T22:30:00Z",
    "dorm": "男生公寓 3 栋"
  }
}
```

---

### 3. 签到历史记录

**接口**: `GET /api/checkin/history`

**请求头**: 需要 token

**查询参数**:
- `page`: 页码
- `pageSize`: 每页数量

**响应示例**:

```json
{
  "success": true,
  "data": {
    "list": [
      {
        "id": 1001,
        "dorm": "男生公寓 3 栋",
        "checkInTime": "2026-05-04T22:30:00Z",
        "location": {
          "latitude": 39.9042,
          "longitude": 116.4074
        }
      }
    ],
    "total": 30,
    "page": 1,
    "pageSize": 20
  }
}
```

---

## 二手物品交易模块

### 1. 获取商品列表

**接口**: `GET /api/secondhand/list`

**查询参数**:
- `category`: 分类（books, electronics, daily, transport）
- `page`: 页码
- `pageSize`: 每页数量

**响应示例**:

```json
{
  "success": true,
  "data": {
    "list": [
      {
        "id": 1,
        "title": "高等数学（第七版）上下册",
        "price": 25.00,
        "image": "https://example.com/book.jpg",
        "category": "books",
        "seller": "李同学",
        "sellerId": 1002,
        "description": "几乎全新，无笔记",
        "status": "available",
        "createdAt": "2026-05-04T10:00:00Z"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

---

### 2. 获取商品详情

**接口**: `GET /api/secondhand/detail`

**查询参数**:
- `id`: 商品ID

**响应示例**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "高等数学（第七版）上下册",
    "price": 25.00,
    "images": ["https://example.com/book1.jpg", "https://example.com/book2.jpg"],
    "category": "books",
    "seller": "李同学",
    "sellerId": 1002,
    "description": "几乎全新，无笔记，可小刀",
    "status": "available",
    "views": 120,
    "createdAt": "2026-05-04T10:00:00Z"
  }
}
```

---

### 3. 发布商品

**接口**: `POST /api/secondhand/publish`

**请求头**: 需要 token

**请求参数**:

```json
{
  "title": "高等数学教材",
  "price": 25.00,
  "category": "books",
  "description": "几乎全新",
  "images": ["base64_image_data"]
}
```

**响应示例**:

```json
{
  "success": true,
  "message": "发布成功",
  "data": {
    "id": 1,
    "title": "高等数学教材"
  }
}
```

---

### 4. 搜索商品

**接口**: `GET /api/secondhand/search`

**查询参数**:
- `keyword`: 搜索关键词
- `category`: 分类（可选）
- `page`: 页码
- `pageSize`: 每页数量

**响应示例**: 同商品列表

---

### 5. 联系卖家

**接口**: `POST /api/secondhand/contact`

**请求头**: 需要 token

**请求参数**:

```json
{
  "productId": 1
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "contact": "微信: zhangsan123",
    "seller": "李同学"
  }
}
```

---

## 代课平台模块

### 1. 获取代课任务列表

**接口**: `GET /api/substitute/list`

**查询参数**:
- `status`: 任务状态（pending, accepted, completed）
- `page`: 页码
- `pageSize`: 每页数量

**响应示例**:

```json
{
  "success": true,
  "data": {
    "list": [
      {
        "id": 1,
        "title": "大学物理（下） 代课/笔记",
        "course": "大学物理",
        "time": "2026-05-05T08:00:00Z",
        "location": "教1-204",
        "reward": "30元 + 奶茶一杯",
        "publisher": "张同学",
        "publisherId": 1003,
        "status": "pending",
        "createdAt": "2026-05-04T10:00:00Z"
      }
    ],
    "total": 10,
    "page": 1,
    "pageSize": 20
  }
}
```

---

### 2. 发布代课需求

**接口**: `POST /api/substitute/publish`

**请求头**: 需要 token

**请求参数**:

```json
{
  "title": "大学物理代课",
  "course": "大学物理",
  "time": "2026-05-05T08:00:00Z",
  "location": "教1-204",
  "reward": "30元",
  "description": "需要记笔记"
}
```

**响应示例**:

```json
{
  "success": true,
  "message": "发布成功",
  "data": {
    "id": 1,
    "title": "大学物理代课"
  }
}
```

---

### 3. 接受代课任务

**接口**: `POST /api/substitute/accept`

**请求头**: 需要 token

**请求参数**:

```json
{
  "taskId": 1
}
```

**响应示例**:

```json
{
  "success": true,
  "message": "接单成功",
  "data": {
    "taskId": 1,
    "publisherContact": "微信: zhangsan123"
  }
}
```

---

## 站长好物模块

### 1. 获取推荐商品列表

**接口**: `GET /api/recommend/list`

**查询参数**:
- `page`: 页码
- `pageSize`: 每页数量

**响应示例**:

```json
{
  "success": true,
  "data": {
    "list": [
      {
        "id": 1,
        "title": "非对称光源屏幕挂灯",
        "price": 69.00,
        "originalPrice": 129.00,
        "description": "宿舍熬夜必备",
        "image": "https://example.com/lamp.jpg",
        "badge": "爆款推荐",
        "link": "https://example.com/product/1"
      }
    ],
    "total": 20,
    "page": 1,
    "pageSize": 20
  }
}
```

---

### 2. 领取优惠券

**接口**: `POST /api/recommend/coupon`

**请求头**: 需要 token

**请求参数**:

```json
{
  "productId": 1
}
```

**响应示例**:

```json
{
  "success": true,
  "message": "优惠券已复制",
  "data": {
    "coupon": "ABCD1234",
    "link": "https://example.com/product/1"
  }
}
```

---

## 学习资源模块

### 1. 获取学习资料列表

**接口**: `GET /api/resources/materials/list`

**查询参数**:
- `category`: 分类（exam, notes, courseware）
- `subject`: 科目
- `page`: 页码
- `pageSize`: 每页数量

**响应示例**:

```json
{
  "success": true,
  "data": {
    "list": [
      {
        "id": 1,
        "title": "高等数学期末复习资料",
        "category": "exam",
        "subject": "数学",
        "fileSize": "2.5MB",
        "downloads": 120,
        "uploader": "李老师",
        "createdAt": "2026-05-01T10:00:00Z"
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 20
  }
}
```

---

### 2. 下载学习资料

**接口**: `GET /api/resources/materials/download`

**请求头**: 需要 token

**查询参数**:
- `id`: 资料ID

**响应**: 文件流

---

### 3. 获取学习软件列表

**接口**: `GET /api/resources/software/list`

**查询参数**:
- `category`: 分类（office, programming, design）
- `page`: 页码
- `pageSize`: 每页数量

**响应示例**:

```json
{
  "success": true,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "Visual Studio Code",
        "category": "programming",
        "version": "1.80.0",
        "size": "85MB",
        "description": "轻量级代码编辑器",
        "downloadLink": "https://example.com/vscode.exe"
      }
    ],
    "total": 30,
    "page": 1,
    "pageSize": 20
  }
}
```

---

## 联系作者模块

### 1. 提交联系表单

**接口**: `POST /api/contact/submit`

**请求参数**:

```json
{
  "name": "张三",
  "email": "zhangsan@example.com",
  "subject": "合作咨询",
  "message": "您好，我想咨询..."
}
```

**响应示例**:

```json
{
  "success": true,
  "message": "提交成功，我们会尽快回复"
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 1001 | 参数错误 |
| 1002 | 用户名已存在 |
| 1003 | 邮箱已被注册 |
| 1004 | 用户名或密码错误 |
| 1005 | Token 无效或已过期 |
| 1006 | 积分不足 |
| 1007 | 今日已签到 |
| 1008 | 兑换码无效 |
| 1009 | 商品不存在 |
| 1010 | 任务已被接受 |
| 2001 | 服务器内部错误 |
| 2002 | 数据库错误 |
| 2003 | 文件上传失败 |

---

## 开发建议

### 1. 环境配置

建议使用以下技术栈：

**后端**:
- Node.js + Express / Koa
- Python + Flask / Django
- Java + Spring Boot

**数据库**:
- MySQL / PostgreSQL（关系型）
- MongoDB（文档型）
- Redis（缓存）

### 2. 安全建议

- 使用 HTTPS 加密传输
- 密码使用 bcrypt 加密存储
- 实现 JWT Token 认证
- 添加请求频率限制（Rate Limiting）
- 实现 CSRF 防护
- 输入参数验证和过滤

### 3. 性能优化

- 使用 Redis 缓存热点数据
- 数据库查询优化和索引
- 图片使用 CDN 加速
- 实现分页加载
- 使用消息队列处理异步任务

### 4. 测试建议

- 单元测试覆盖核心业务逻辑
- 接口测试使用 Postman / Swagger
- 压力测试评估系统性能
- 安全测试防范常见漏洞

---

## 联系方式

如有疑问，请联系：
- 邮箱: admin@mybrand.com
- 文档版本: v1.0
- 更新日期: 2026-05-04
