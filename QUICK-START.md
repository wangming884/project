# 🚀 快速开始指南

## 📋 目录

1. [前端快速开始](#前端快速开始)
2. [后端快速开始](#后端快速开始)
3. [完整开发流程](#完整开发流程)
4. [常见问题](#常见问题)

---

## 前端快速开始

### 方式一：直接使用（无需后端）

前端已经实现了降级方案，即使没有后端也可以正常使用基本功能。

1. **打开项目**
   ```bash
   # 使用任意 Web 服务器打开
   # 例如使用 Python
   python -m http.server 8080
   
   # 或使用 Node.js
   npx serve
   
   # 或使用 VS Code 的 Live Server 插件
   ```

2. **访问页面**
   ```
   http://localhost:8080/index.html
   ```

3. **测试功能**
   - 登录/注册（使用本地存储）
   - 每日签到（使用本地存储）
   - 晚寝签到（使用本地存储）
   - 浏览二手商品（静态数据）

### 方式二：连接后端 API

1. **修改 API 配置**
   
   编辑 `js/api-config.js`：
   ```javascript
   const API_BASE_URL = 'http://localhost:3000/api';
   ```

2. **启动后端服务**（见下一节）

3. **刷新页面**
   
   前端会自动连接后端 API

---

## 后端快速开始

### 使用示例后端（推荐新手）

1. **进入后端目录**
   ```bash
   cd backend-example
   ```

2. **安装依赖**
   ```bash
   npm install
   ```

3. **启动服务器**
   ```bash
   npm start
   ```

4. **查看启动信息**
   ```
   🚀 服务器已启动！
   📍 地址: http://localhost:3000
   
   📝 测试账号:
      用户名: demo
      密码: 123456
   ```

5. **测试接口**
   
   使用浏览器或 Postman 访问：
   ```
   http://localhost:3000/api/secondhand/list
   ```

### 使用自己的后端

参考 `API-DOCUMENTATION.md` 实现接口。

推荐技术栈：
- **Node.js**: Express / Koa / NestJS
- **Python**: Flask / Django / FastAPI
- **Java**: Spring Boot
- **Go**: Gin / Echo

---

## 完整开发流程

### 第一步：环境准备

1. **安装 Node.js**
   - 下载：https://nodejs.org/
   - 验证：`node -v` 和 `npm -v`

2. **安装代码编辑器**
   - 推荐：VS Code (https://code.visualstudio.com/)

3. **安装 Git**（可选）
   - 下载：https://git-scm.com/

### 第二步：启动前端

```bash
# 方式 1: 使用 Python（如果已安装）
python -m http.server 8080

# 方式 2: 使用 Node.js
npx serve

# 方式 3: 使用 VS Code Live Server 插件
# 右键 index.html -> Open with Live Server
```

访问：http://localhost:8080/index.html

### 第三步：启动后端（可选）

```bash
# 进入后端目录
cd backend-example

# 安装依赖（首次运行）
npm install

# 启动服务器
npm start
```

### 第四步：测试功能

1. **测试登录**
   - 打开首页
   - 点击"登录"按钮
   - 输入测试账号：
     - 用户名: `demo`
     - 密码: `123456`

2. **测试签到**
   - 登录后进入服务大厅
   - 点击"签到领积分"按钮
   - 查看积分变化

3. **测试晚寝签到**
   - 进入"晚寝快速签到"模块
   - 填写信息并提交
   - 查看签到结果

4. **测试二手交易**
   - 进入"二手物品交易"模块
   - 浏览商品列表
   - 测试搜索功能

---

## 常见问题

### Q1: 前端无法连接后端？

**检查清单**:
- ✅ 后端服务是否启动？
- ✅ API 地址是否正确？（`js/api-config.js`）
- ✅ 浏览器控制台是否有 CORS 错误？

**解决方案**:
```javascript
// 确认 API_BASE_URL 配置正确
const API_BASE_URL = 'http://localhost:3000/api';
```

### Q2: 登录后提示 Token 无效？

**原因**: 后端 SECRET_KEY 可能已更改

**解决方案**:
1. 清除浏览器 localStorage
2. 重新登录

```javascript
// 在浏览器控制台执行
localStorage.clear();
```

### Q3: 积分数据不同步？

**原因**: 前端使用了本地存储降级方案

**解决方案**:
1. 确保后端服务正常运行
2. 刷新页面重新获取数据
3. 或清除本地存储：
   ```javascript
   localStorage.removeItem('userPoints');
   ```

### Q4: 如何查看 API 请求？

**方法 1**: 浏览器开发者工具
1. 按 F12 打开开发者工具
2. 切换到 "Network" 标签
3. 刷新页面查看请求

**方法 2**: 查看控制台日志
```javascript
// API 请求会自动打印错误日志
console.log('API Request Error:', error);
```

### Q5: 如何添加新的 API 接口？

**步骤**:

1. **在 `js/api-config.js` 添加接口地址**
   ```javascript
   const API_ENDPOINTS = {
       // ... 现有接口
       myModule: {
           getData: `${API_BASE_URL}/my-module/data`,
       }
   };
   ```

2. **在页面中调用**
   ```javascript
   async function loadData() {
       try {
           showLoading('加载中...');
           const response = await get(API_ENDPOINTS.myModule.getData);
           hideLoading();
           
           if (response.success) {
               // 处理数据
           }
       } catch (error) {
           handleError(error, '加载失败');
       }
   }
   ```

3. **在后端实现接口**
   ```javascript
   app.get('/api/my-module/data', authenticateToken, (req, res) => {
       res.json({
           success: true,
           data: { /* 你的数据 */ }
       });
   });
   ```

### Q6: 如何部署到生产环境？

**前端部署**:

1. **修改 API 配置**
   ```javascript
   // js/api-config.js
   const API_BASE_URL = 'https://your-domain.com/api';
   ```

2. **上传文件到服务器**
   - 使用 FTP/SFTP 上传所有文件
   - 或使用 Git 部署

3. **配置 Web 服务器**
   - Nginx / Apache 配置静态文件服务

**后端部署**:

1. **使用 PM2 管理进程**
   ```bash
   npm install -g pm2
   pm2 start server.js --name campus-api
   pm2 save
   pm2 startup
   ```

2. **配置反向代理**（Nginx 示例）
   ```nginx
   location /api {
       proxy_pass http://localhost:3000;
       proxy_http_version 1.1;
       proxy_set_header Upgrade $http_upgrade;
       proxy_set_header Connection 'upgrade';
       proxy_set_header Host $host;
       proxy_cache_bypass $http_upgrade;
   }
   ```

3. **配置 HTTPS**
   - 使用 Let's Encrypt 免费证书
   - 或购买商业证书

### Q7: 如何连接真实数据库？

**MySQL 示例**:

1. **安装依赖**
   ```bash
   npm install mysql2
   ```

2. **创建数据库连接**
   ```javascript
   const mysql = require('mysql2/promise');
   
   const pool = mysql.createPool({
       host: 'localhost',
       user: 'root',
       password: 'your_password',
       database: 'campus_platform',
       waitForConnections: true,
       connectionLimit: 10,
   });
   ```

3. **修改接口实现**
   ```javascript
   app.post('/api/auth/login', async (req, res) => {
       const { username, password } = req.body;
       
       // 查询数据库
       const [rows] = await pool.execute(
           'SELECT * FROM users WHERE username = ? OR email = ?',
           [username, username]
       );
       
       if (rows.length === 0) {
           return res.json({
               success: false,
               message: '用户不存在'
           });
       }
       
       const user = rows[0];
       // ... 验证密码等逻辑
   });
   ```

### Q8: 如何调试前端代码？

**方法**:

1. **使用 console.log**
   ```javascript
   console.log('用户数据:', userData);
   console.log('API 响应:', response);
   ```

2. **使用浏览器断点**
   - 在开发者工具的 Sources 标签设置断点
   - 或在代码中添加 `debugger;`

3. **查看网络请求**
   - F12 -> Network 标签
   - 查看请求和响应数据

4. **使用 Vue DevTools / React DevTools**（如果使用框架）

---

## 📚 推荐学习路径

### 前端开发者

1. **基础知识**
   - HTML / CSS / JavaScript
   - ES6+ 新特性
   - Fetch API / Axios

2. **进阶知识**
   - 前端框架（Vue / React / Angular）
   - 状态管理（Vuex / Redux）
   - 构建工具（Webpack / Vite）

3. **推荐资源**
   - MDN Web Docs: https://developer.mozilla.org/
   - JavaScript.info: https://javascript.info/
   - 菜鸟教程: https://www.runoob.com/

### 后端开发者

1. **基础知识**
   - Node.js / Python / Java 基础
   - HTTP 协议
   - RESTful API 设计

2. **进阶知识**
   - 数据库设计（MySQL / MongoDB）
   - 认证授权（JWT / OAuth）
   - 缓存（Redis）
   - 消息队列（RabbitMQ / Kafka）

3. **推荐资源**
   - Node.js 官方文档: https://nodejs.org/docs/
   - Express.js 文档: https://expressjs.com/
   - 数据库教程: https://www.runoob.com/mysql/

---

## 🎯 下一步计划

### 短期目标（1-2周）

- [ ] 完成所有 API 接口实现
- [ ] 连接真实数据库
- [ ] 实现文件上传功能
- [ ] 添加用户头像功能

### 中期目标（1个月）

- [ ] 实现支付功能
- [ ] 添加管理后台
- [ ] 实现消息通知
- [ ] 优化性能

### 长期目标（3个月）

- [ ] 移动端适配
- [ ] 开发小程序版本
- [ ] 实现数据分析
- [ ] 上线运营

---

## 📞 获取帮助

### 文档资源
- 📖 `README.md` - 项目说明
- 📚 `API-DOCUMENTATION.md` - 接口文档
- 📊 `OPTIMIZATION-SUMMARY.md` - 优化总结
- 💻 `backend-example/README.md` - 后端指南

### 在线资源
- Stack Overflow: https://stackoverflow.com/
- GitHub Issues: 提交问题和建议
- 开发者社区: 掘金、CSDN、博客园

### 联系方式
- 📧 邮箱: admin@mybrand.com

---

**祝你开发顺利！如有问题，随时查阅文档或寻求帮助。🎉**
