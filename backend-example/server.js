/**
 * 校园综合服务平台 - 后端示例（Node.js + Express）
 * 这是一个简单的后端示例，展示如何实现基本的 API 接口
 * 
 * 安装依赖：
 * npm init -y
 * npm install express cors body-parser jsonwebtoken bcrypt
 * 
 * 运行：
 * node server.js
 */

const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');

const app = express();
const PORT = 3000;
const SECRET_KEY = 'your-secret-key-change-in-production';

// 中间件配置
app.use(cors({
    origin: true, // 允许所有来源（生产环境应该指定具体域名）
    credentials: true
}));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// ==================== 模拟数据库 ====================
// 生产环境应该使用真实数据库（MySQL, MongoDB 等）

const users = [
    {
        id: 1,
        username: 'demo',
        email: 'demo@example.com',
        password: bcrypt.hashSync('123456', 10), // 密码: 123456
        points: 100,
        lastSignInDate: null,
        createdAt: new Date().toISOString()
    }
];

const pointsHistory = [];
const checkinRecords = [];
const secondhandProducts = [
    {
        id: 1,
        title: '高等数学（第七版）上下册 几乎全新',
        price: 25.00,
        image: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400',
        category: 'books',
        seller: '李同学',
        sellerId: 1,
        description: '几乎全新，无笔记',
        status: 'available',
        views: 120,
        createdAt: new Date().toISOString()
    },
    {
        id: 2,
        title: '九成新 索尼降噪耳机 毕业出清',
        price: 450.00,
        image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&q=80&w=400',
        category: 'electronics',
        seller: '王学长',
        sellerId: 1,
        description: '九成新，音质完美',
        status: 'available',
        views: 85,
        createdAt: new Date().toISOString()
    }
];

// ==================== 工具函数 ====================

// 生成 Token
function generateToken(userId) {
    return jwt.sign({ userId }, SECRET_KEY, { expiresIn: '7d' });
}

// 验证 Token 中间件
function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
        return res.status(401).json({
            success: false,
            message: '未提供认证令牌'
        });
    }

    jwt.verify(token, SECRET_KEY, (err, decoded) => {
        if (err) {
            return res.status(401).json({
                success: false,
                message: 'Token 无效或已过期'
            });
        }
        req.userId = decoded.userId;
        next();
    });
}

// 查找用户
function findUserById(id) {
    return users.find(u => u.id === id);
}

function findUserByUsername(username) {
    return users.find(u => u.username === username || u.email === username);
}

// ==================== 用户认证接口 ====================

// 用户注册
app.post('/api/auth/register', async (req, res) => {
    const { username, email, password } = req.body;

    // 参数验证
    if (!username || !email || !password) {
        return res.status(400).json({
            success: false,
            message: '请填写完整的注册信息'
        });
    }

    // 检查用户名是否已存在
    if (users.find(u => u.username === username)) {
        return res.status(400).json({
            success: false,
            message: '用户名已存在'
        });
    }

    // 检查邮箱是否已存在
    if (users.find(u => u.email === email)) {
        return res.status(400).json({
            success: false,
            message: '邮箱已被注册'
        });
    }

    // 创建新用户
    const newUser = {
        id: users.length + 1,
        username,
        email,
        password: bcrypt.hashSync(password, 10),
        points: 0,
        lastSignInDate: null,
        createdAt: new Date().toISOString()
    };

    users.push(newUser);

    res.json({
        success: true,
        message: '注册成功',
        data: {
            userId: newUser.id,
            username: newUser.username,
            email: newUser.email
        }
    });
});

// 用户登录
app.post('/api/auth/login', (req, res) => {
    const { username, password } = req.body;

    // 参数验证
    if (!username || !password) {
        return res.status(400).json({
            success: false,
            message: '请填写完整的登录信息'
        });
    }

    // 查找用户
    const user = findUserByUsername(username);

    if (!user || !bcrypt.compareSync(password, user.password)) {
        return res.status(400).json({
            success: false,
            message: '用户名或密码错误'
        });
    }

    // 生成 Token
    const token = generateToken(user.id);

    res.json({
        success: true,
        message: '登录成功',
        data: {
            token,
            user: {
                userId: user.id,
                username: user.username,
                email: user.email,
                points: user.points
            }
        }
    });
});

// 获取用户信息
app.get('/api/auth/user', authenticateToken, (req, res) => {
    const user = findUserById(req.userId);

    if (!user) {
        return res.status(404).json({
            success: false,
            message: '用户不存在'
        });
    }

    res.json({
        success: true,
        data: {
            userId: user.id,
            username: user.username,
            email: user.email,
            points: user.points,
            createdAt: user.createdAt
        }
    });
});

// ==================== 积分系统接口 ====================

// 获取积分余额
app.get('/api/points/balance', authenticateToken, (req, res) => {
    const user = findUserById(req.userId);

    res.json({
        success: true,
        data: {
            balance: user.points,
            lastSignInDate: user.lastSignInDate
        }
    });
});

// 每日签到
app.post('/api/points/sign-in', authenticateToken, (req, res) => {
    const user = findUserById(req.userId);
    const today = new Date().toDateString();

    // 检查今天是否已签到
    if (user.lastSignInDate === today) {
        return res.status(400).json({
            success: false,
            message: '今日已签到'
        });
    }

    // 增加积分
    const earnedPoints = 1;
    user.points += earnedPoints;
    user.lastSignInDate = today;

    // 记录积分历史
    pointsHistory.push({
        id: pointsHistory.length + 1,
        userId: user.id,
        type: 'sign_in',
        amount: earnedPoints,
        balance: user.points,
        description: '每日签到',
        createdAt: new Date().toISOString()
    });

    res.json({
        success: true,
        message: '签到成功',
        data: {
            earnedPoints,
            balance: user.points,
            continuousDays: 1 // 简化处理，实际应该计算连续天数
        }
    });
});

// 兑换积分码
app.post('/api/points/redeem', authenticateToken, (req, res) => {
    const { code } = req.body;
    const user = findUserById(req.userId);

    if (!code) {
        return res.status(400).json({
            success: false,
            message: '请输入兑换码'
        });
    }

    // 简化处理：任意兑换码都给 50 积分
    // 实际应该验证兑换码的有效性和是否已使用
    const points = 50;
    user.points += points;

    // 记录积分历史
    pointsHistory.push({
        id: pointsHistory.length + 1,
        userId: user.id,
        type: 'redeem',
        amount: points,
        balance: user.points,
        description: `兑换码: ${code}`,
        createdAt: new Date().toISOString()
    });

    res.json({
        success: true,
        message: '兑换成功',
        data: {
            points,
            balance: user.points
        }
    });
});

// 积分历史记录
app.get('/api/points/history', authenticateToken, (req, res) => {
    const { page = 1, pageSize = 20 } = req.query;
    
    const userHistory = pointsHistory
        .filter(h => h.userId === req.userId)
        .reverse();

    const start = (page - 1) * pageSize;
    const end = start + parseInt(pageSize);
    const list = userHistory.slice(start, end);

    res.json({
        success: true,
        data: {
            list,
            total: userHistory.length,
            page: parseInt(page),
            pageSize: parseInt(pageSize)
        }
    });
});

// ==================== 晚寝签到接口 ====================

// 提交晚寝签到
app.post('/api/checkin/submit', authenticateToken, (req, res) => {
    const { account, password, dorm, location } = req.body;
    const user = findUserById(req.userId);

    // 参数验证
    if (!account || !password || !dorm) {
        return res.status(400).json({
            success: false,
            message: '请填写完整的签到信息'
        });
    }

    // 检查积分是否足够
    if (user.points < 10) {
        return res.status(400).json({
            success: false,
            message: '积分不足，需要 10 积分'
        });
    }

    // 检查今天是否已签到
    const today = new Date().toDateString();
    const todayCheckin = checkinRecords.find(
        r => r.userId === user.id && new Date(r.createdAt).toDateString() === today
    );

    if (todayCheckin) {
        return res.status(400).json({
            success: false,
            message: '今晚已完成签到'
        });
    }

    // 扣除积分
    user.points -= 10;

    // 记录签到
    const checkin = {
        id: checkinRecords.length + 1,
        userId: user.id,
        account,
        dorm,
        location,
        createdAt: new Date().toISOString()
    };
    checkinRecords.push(checkin);

    // 记录积分历史
    pointsHistory.push({
        id: pointsHistory.length + 1,
        userId: user.id,
        type: 'checkin',
        amount: -10,
        balance: user.points,
        description: '晚寝签到',
        createdAt: new Date().toISOString()
    });

    res.json({
        success: true,
        message: '签到成功',
        data: {
            checkInId: checkin.id,
            remainingPoints: user.points,
            checkInTime: checkin.createdAt
        }
    });
});

// 获取签到状态
app.get('/api/checkin/status', authenticateToken, (req, res) => {
    const today = new Date().toDateString();
    const todayCheckin = checkinRecords.find(
        r => r.userId === req.userId && new Date(r.createdAt).toDateString() === today
    );

    res.json({
        success: true,
        data: {
            hasCheckedInToday: !!todayCheckin,
            lastCheckInTime: todayCheckin ? todayCheckin.createdAt : null,
            dorm: todayCheckin ? todayCheckin.dorm : null
        }
    });
});

// ==================== 二手物品交易接口 ====================

// 获取商品列表
app.get('/api/secondhand/list', (req, res) => {
    const { category, page = 1, pageSize = 20 } = req.query;

    let filteredProducts = secondhandProducts;

    // 按分类筛选
    if (category) {
        filteredProducts = filteredProducts.filter(p => p.category === category);
    }

    // 分页
    const start = (page - 1) * pageSize;
    const end = start + parseInt(pageSize);
    const list = filteredProducts.slice(start, end);

    res.json({
        success: true,
        data: {
            list,
            total: filteredProducts.length,
            page: parseInt(page),
            pageSize: parseInt(pageSize)
        }
    });
});

// 搜索商品
app.get('/api/secondhand/search', (req, res) => {
    const { keyword, category, page = 1, pageSize = 20 } = req.query;

    let filteredProducts = secondhandProducts;

    // 按关键词搜索
    if (keyword) {
        filteredProducts = filteredProducts.filter(p =>
            p.title.includes(keyword) || p.description.includes(keyword)
        );
    }

    // 按分类筛选
    if (category) {
        filteredProducts = filteredProducts.filter(p => p.category === category);
    }

    // 分页
    const start = (page - 1) * pageSize;
    const end = start + parseInt(pageSize);
    const list = filteredProducts.slice(start, end);

    res.json({
        success: true,
        data: {
            list,
            total: filteredProducts.length,
            page: parseInt(page),
            pageSize: parseInt(pageSize)
        }
    });
});

// 联系卖家
app.post('/api/secondhand/contact', authenticateToken, (req, res) => {
    const { productId } = req.body;

    const product = secondhandProducts.find(p => p.id === parseInt(productId));

    if (!product) {
        return res.status(404).json({
            success: false,
            message: '商品不存在'
        });
    }

    // 返回卖家联系方式（实际应该从用户表获取）
    res.json({
        success: true,
        data: {
            contact: '微信: seller123',
            seller: product.seller
        }
    });
});

// ==================== 启动服务器 ====================

app.listen(PORT, () => {
    console.log(`\n🚀 服务器已启动！`);
    console.log(`📍 地址: http://localhost:${PORT}`);
    console.log(`\n📝 测试账号:`);
    console.log(`   用户名: demo`);
    console.log(`   密码: 123456`);
    console.log(`\n💡 提示: 前端需要修改 API_BASE_URL 为 http://localhost:${PORT}/api\n`);
});
