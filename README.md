# 校园综合服务平台 - 前端优化说明

## 📦 项目概述

这是一个校园综合服务平台的前端项目，提供以下功能模块：

- 🔐 用户认证（登录/注册）
- 💰 积分系统（签到、兑换）
- 🌙 晚寝签到
- 📦 二手物品交易
- 🤝 互助代课平台
- 🎁 站长好物推荐
- 📚 学习资源下载
- 📧 联系作者

## 🎯 本次优化内容

### 1. 统一的 API 配置管理

**文件**: `js/api-config.js`

- 集中管理所有后端接口地址
- 支持开发环境和生产环境自动切换
- 按功能模块分类组织接口
- 便于维护和更新

```javascript
// 使用示例
const loginUrl = API_ENDPOINTS.auth.login;
const pointsUrl = API_ENDPOINTS.points.getBalance;
```

### 2. 通用工具函数库

**文件**: `js/api-utils.js`

提供以下功能：

#### HTTP 请求封装
- `get(url, params)` - GET 请求
- `post(url, data)` - POST 请求
- `put(url, data)` - PUT 请求
- `del(url, data)` - DELETE 请求
- 自动携带 token 认证
- 统一错误处理

#### 加载状态管理
- `showLoading(message)` - 显示加载动画
- `hideLoading()` - 隐藏加载动画

#### 消息提示
- `showMessage(message, type, duration)` - 显示提示消息
- 支持 success、error、warning、info 四种类型
- 自动消失，带动画效果

#### 错误处理
- `handleError(error, defaultMessage)` - 统一错误处理
- 自动识别常见 HTTP 错误
- 401 错误自动跳转登录页

#### 本地存储工具
- `setStorage(key, value)` - 存储数据（支持对象）
- `getStorage(key, defaultValue)` - 获取数据（自动解析 JSON）
- `removeStorage(key)` - 删除数据
- `clearStorage()` - 清空存储

#### 表单验证
- `validateEmail(email)` - 验证邮箱格式
- `validatePhone(phone)` - 验证手机号格式
- `validatePassword(password)` - 验证密码强度

#### 日期时间工具
- `formatDate(date, format)` - 格式化日期时间
- `getRelativeTime(date)` - 获取相对时间（如：5分钟前）

### 3. 页面功能优化

#### index.html（首页）
- ✅ 集成登录 API 调用
- ✅ 集成注册 API 调用
- ✅ 添加表单验证
- ✅ 优化用户体验（加载动画、消息提示）
- ✅ 登录成功自动跳转
- ✅ 注册成功自动填充登录表单

#### main.html（服务大厅）
- ✅ 集成积分查询 API
- ✅ 集成每日签到 API
- ✅ 支持后端未连接时的降级方案（使用本地存储）
- ✅ 优化签到体验

#### checkin.html（晚寝签到）
- ✅ 集成签到提交 API
- ✅ 集成积分兑换 API
- ✅ 添加地理位置获取功能
- ✅ 优化表单验证
- ✅ 支持降级方案

#### secondhand.html（二手交易）
- ✅ 集成商品列表 API
- ✅ 集成商品搜索 API
- ✅ 集成联系卖家 API
- ✅ 动态渲染商品列表
- ✅ 分类筛选功能
- ✅ 搜索功能

### 4. 完整的 API 接口文档

**文件**: `API-DOCUMENTATION.md`

- 📋 详细的接口说明
- 📝 请求参数示例
- 📤 响应数据示例
- 🔒 认证授权说明
- ⚠️ 错误码说明
- 💡 开发建议

涵盖所有功能模块的接口定义，方便后端开发对接。

## 🚀 使用方法

### 1. 引入依赖

在 HTML 页面中引入 API 配置和工具函数：

```html
<!-- 引入 API 配置和工具函数 -->
<script src="js/api-config.js"></script>
<script src="js/api-utils.js"></script>
```

### 2. 调用 API 示例

#### 登录示例

```javascript
async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    try {
        showLoading('登录中...');
        
        const response = await post(API_ENDPOINTS.auth.login, {
            username,
            password,
        });
        
        hideLoading();
        
        if (response.success) {
            setStorage('authToken', response.token);
            setStorage('userInfo', response.user);
            showMessage('登录成功！', 'success');
            window.location.href = 'main.html';
        } else {
            showMessage(response.message || '登录失败', 'error');
        }
    } catch (error) {
        handleError(error, '登录失败');
    }
}
```

#### 获取数据示例

```javascript
async function loadProducts() {
    try {
        showLoading('加载中...');
        
        const response = await get(API_ENDPOINTS.secondhand.list, {
            category: 'books',
            page: 1,
            pageSize: 20,
        });
        
        hideLoading();
        
        if (response.success) {
            renderProducts(response.data.list);
        }
    } catch (error) {
        handleError(error, '加载失败');
    }
}
```

### 3. 降级方案

当后端服务未连接时，前端会自动使用本地存储作为降级方案：

```javascript
try {
    // 尝试调用后端 API
    const response = await post(API_ENDPOINTS.points.dailySignIn);
    // 处理响应...
} catch (error) {
    // 降级到本地处理
    console.warn('后端未连接，使用本地存储');
    userPoints += 1;
    localStorage.setItem('userPoints', userPoints);
    showMessage('签到成功！', 'success');
}
```

## 🔧 后端开发指南

### 1. 环境搭建

推荐技术栈：

**Node.js + Express**
```bash
npm init -y
npm install express cors body-parser jsonwebtoken bcrypt mysql2
```

**Python + Flask**
```bash
pip install flask flask-cors flask-jwt-extended bcrypt pymysql
```

### 2. 配置 CORS

允许前端跨域请求：

```javascript
// Node.js + Express
const cors = require('cors');
app.use(cors({
    origin: 'http://localhost:8080',
    credentials: true
}));
```

```python
# Python + Flask
from flask_cors import CORS
CORS(app, supports_credentials=True)
```

### 3. 实现接口

参考 `API-DOCUMENTATION.md` 文档实现各个接口。

示例（Node.js + Express）：

```javascript
// 登录接口
app.post('/api/auth/login', async (req, res) => {
    const { username, password } = req.body;
    
    // 查询用户
    const user = await User.findOne({ username });
    
    if (!user || !bcrypt.compareSync(password, user.password)) {
        return res.json({
            success: false,
            message: '用户名或密码错误'
        });
    }
    
    // 生成 token
    const token = jwt.sign({ userId: user.id }, 'secret_key');
    
    res.json({
        success: true,
        message: '登录成功',
        data: {
            token,
            user: {
                userId: user.id,
                username: user.username,
                email: user.email
            }
        }
    });
});
```

### 4. 数据库设计

参考以下表结构：

**users 表**（用户表）
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**points_history 表**（积分历史）
```sql
CREATE TABLE points_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount INT NOT NULL,
    balance INT NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**checkin_records 表**（签到记录）
```sql
CREATE TABLE checkin_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    account VARCHAR(50) NOT NULL,
    dorm VARCHAR(100) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**secondhand_products 表**（二手商品）
```sql
CREATE TABLE secondhand_products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50),
    description TEXT,
    image VARCHAR(500),
    seller_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'available',
    views INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(id)
);
```

## 📁 项目结构

```
.
├── index.html              # 首页（登录/注册）
├── main.html               # 服务大厅
├── checkin.html            # 晚寝签到
├── secondhand.html         # 二手交易
├── substitute.html         # 代课平台
├── recommend.html          # 站长好物
├── resources.html          # 学习资源
├── learning_materials.html # 学习资料
├── learning_software.html  # 学习软件
├── publish.html            # 发布商品
├── contact.html            # 联系作者
├── view.jpg                # 展示图片
├── js/
│   ├── api-config.js       # API 配置文件
│   └── api-utils.js        # 工具函数库
├── API-DOCUMENTATION.md    # API 接口文档
└── README.md               # 项目说明
```

## 🎨 特性说明

### 1. 优雅的用户体验

- ✨ 加载动画：所有异步操作都有加载提示
- 💬 消息提示：操作结果实时反馈
- 🎯 表单验证：前端验证提升用户体验
- 🔄 自动跳转：登录成功自动跳转到主页

### 2. 健壮的错误处理

- 🛡️ 统一错误处理机制
- 🔌 后端未连接时的降级方案
- 🔐 Token 过期自动跳转登录
- 📱 网络错误友好提示

### 3. 灵活的配置管理

- 🌍 环境自动识别（开发/生产）
- 📝 接口地址集中管理
- 🔧 易于维护和扩展

### 4. 完善的文档

- 📚 详细的 API 接口文档
- 💡 代码注释清晰
- 🎓 使用示例丰富

## 🔒 安全建议

1. **密码安全**
   - 前端不存储明文密码
   - 使用 HTTPS 传输
   - 后端使用 bcrypt 加密

2. **Token 管理**
   - Token 存储在 localStorage
   - 设置合理的过期时间
   - 定期刷新 Token

3. **输入验证**
   - 前端验证提升体验
   - 后端验证保证安全
   - 防止 SQL 注入和 XSS 攻击

4. **权限控制**
   - 接口需要 Token 认证
   - 验证用户权限
   - 防止越权操作

## 📞 技术支持

如有问题，请联系：
- 📧 邮箱: admin@mybrand.com
- 📖 文档: 查看 API-DOCUMENTATION.md

## 📝 更新日志

### v1.0 (2026-05-04)

- ✅ 创建统一的 API 配置文件
- ✅ 创建通用工具函数库
- ✅ 优化首页登录注册功能
- ✅ 优化服务大厅积分系统
- ✅ 优化晚寝签到功能
- ✅ 优化二手交易功能
- ✅ 编写完整的 API 接口文档
- ✅ 添加降级方案支持

## 📄 许可证

MIT License

---

**祝开发顺利！🎉**
