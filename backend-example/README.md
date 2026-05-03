# 后端示例 - 快速启动指南

这是一个简单的 Node.js + Express 后端示例，帮助你快速启动开发。

## 🚀 快速开始

### 1. 安装依赖

```bash
cd backend-example
npm install
```

### 2. 启动服务器

```bash
npm start
```

或者使用开发模式（自动重启）：

```bash
npm run dev
```

### 3. 测试接口

服务器启动后，访问：http://localhost:3000

**测试账号**：
- 用户名: `demo`
- 密码: `123456`

## 📝 已实现的接口

### 用户认证
- ✅ POST `/api/auth/register` - 用户注册
- ✅ POST `/api/auth/login` - 用户登录
- ✅ GET `/api/auth/user` - 获取用户信息

### 积分系统
- ✅ GET `/api/points/balance` - 获取积分余额
- ✅ POST `/api/points/sign-in` - 每日签到
- ✅ POST `/api/points/redeem` - 兑换积分码
- ✅ GET `/api/points/history` - 积分历史记录

### 晚寝签到
- ✅ POST `/api/checkin/submit` - 提交晚寝签到
- ✅ GET `/api/checkin/status` - 获取签到状态

### 二手物品交易
- ✅ GET `/api/secondhand/list` - 获取商品列表
- ✅ GET `/api/secondhand/search` - 搜索商品
- ✅ POST `/api/secondhand/contact` - 联系卖家

## 🔧 配置前端

修改前端的 `js/api-config.js` 文件：

```javascript
const API_BASE_URL = 'http://localhost:3000/api';
```

## 📦 依赖说明

- **express**: Web 框架
- **cors**: 跨域资源共享
- **body-parser**: 请求体解析
- **jsonwebtoken**: JWT Token 生成和验证
- **bcrypt**: 密码加密

## 🗄️ 数据存储

当前使用内存存储（重启后数据丢失），生产环境应该使用：
- MySQL / PostgreSQL（关系型数据库）
- MongoDB（文档型数据库）
- Redis（缓存）

## 🔐 安全提示

⚠️ 这只是一个示例，生产环境需要：

1. 使用真实数据库
2. 修改 SECRET_KEY
3. 添加请求频率限制
4. 实现更完善的错误处理
5. 添加日志记录
6. 使用 HTTPS
7. 实现更严格的输入验证

## 📚 下一步

1. 连接真实数据库
2. 实现剩余的 API 接口
3. 添加文件上传功能
4. 实现支付功能
5. 添加管理后台

## 💡 提示

使用 Postman 或其他 API 测试工具测试接口：

**登录示例**：
```
POST http://localhost:3000/api/auth/login
Content-Type: application/json

{
  "username": "demo",
  "password": "123456"
}
```

**获取积分（需要 Token）**：
```
GET http://localhost:3000/api/points/balance
Authorization: Bearer {your_token}
```
