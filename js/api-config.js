/**
 * API 配置文件
 * 统一管理所有后端接口地址
 */

const API_CONFIG_STORAGE_KEY = 'apiBaseUrl';

function normalizeApiBaseUrl(url) {
    if (!url) {
        return '/api';
    }
    return String(url).replace(/\/+$/, '');
}

function readApiBaseUrlOverride() {
    try {
        const params = new URLSearchParams(window.location.search);
        const queryValue = params.get('apiBaseUrl');
        if (queryValue) {
            localStorage.setItem(API_CONFIG_STORAGE_KEY, queryValue);
            return queryValue;
        }

        if (window.__API_BASE_URL__) {
            return window.__API_BASE_URL__;
        }

        const storedValue = localStorage.getItem(API_CONFIG_STORAGE_KEY);
        if (storedValue) {
            return storedValue;
        }
    } catch (error) {
        console.warn('读取 API 地址覆盖配置失败:', error);
    }
    return '';
}

function buildDefaultApiBaseUrl() {
    const isLocalhost = ['localhost', '127.0.0.1'].includes(window.location.hostname);
    if (isLocalhost) {
        // 当前主架构为静态前端 + Java 后端，开发环境默认对接 8080。
        return 'http://localhost:8080/api';
    }
    return '/api';
}

function resolveApiBaseUrl() {
    return normalizeApiBaseUrl(readApiBaseUrlOverride() || buildDefaultApiBaseUrl());
}

function setApiBaseUrl(url) {
    const nextValue = normalizeApiBaseUrl(url);
    try {
        localStorage.setItem(API_CONFIG_STORAGE_KEY, nextValue);
    } catch (error) {
        console.warn('保存 API 地址配置失败:', error);
    }
    return nextValue;
}

// API 基础地址配置
const API_BASE_URL = resolveApiBaseUrl();

// API 接口路径配置
const API_ENDPOINTS = {
    // 用户认证相关
    auth: {
        login: `${API_BASE_URL}/auth/login`,           // POST - 用户登录
        adminLogin: `${API_BASE_URL}/auth/admin-login`, // POST - 管理员登录
        register: `${API_BASE_URL}/auth/register`,     // POST - 用户注册
        logout: `${API_BASE_URL}/auth/logout`,         // POST - 用户登出
        checkAuth: `${API_BASE_URL}/auth/check`,       // GET - 检查登录状态
        getUserInfo: `${API_BASE_URL}/auth/user`,      // GET - 获取用户信息
    },

    // 积分系统相关
    points: {
        getBalance: `${API_BASE_URL}/points/balance`,          // GET - 获取积分余额
        dailySignIn: `${API_BASE_URL}/points/sign-in`,         // POST - 每日签到
        redeem: `${API_BASE_URL}/points/redeem`,               // POST - 兑换积分码
        purchase: `${API_BASE_URL}/points/purchase`,           // POST - 购买积分套餐
        getHistory: `${API_BASE_URL}/points/history`,          // GET - 积分历史记录
    },

    // 公告相关
    announcement: {
        latest: `${API_BASE_URL}/announcement/latest`,         // GET - 获取最新公告
    },

    // 晚寝签到相关
    checkIn: {
        submit: `${API_BASE_URL}/checkin/submit`,              // POST - 提交晚寝签到
        getStatus: `${API_BASE_URL}/checkin/status`,           // GET - 获取签到状态
        getHistory: `${API_BASE_URL}/checkin/history`,         // GET - 签到历史记录
        getLocation: `${API_BASE_URL}/checkin/location`,       // GET - 获取定位信息
        automationSpec: `${API_BASE_URL}/checkin/automation/spec`, // GET - 自动化签到接入说明
        automationSubmit: `${API_BASE_URL}/checkin/automation/submit`, // POST - 自动化签到脚本入口
    },

    // 二手物品交易相关
    secondhand: {
        list: `${API_BASE_URL}/secondhand/list`,               // GET - 兼容旧版商品列表
        detail: `${API_BASE_URL}/secondhand/detail`,           // GET - 兼容旧版商品详情
        publish: `${API_BASE_URL}/secondhand/publish`,         // POST - 发布商品
        search: `${API_BASE_URL}/secondhand/search`,           // GET - 兼容旧版搜索商品
        contact: `${API_BASE_URL}/secondhand/contact`,         // POST - 兼容旧版联系卖家
        products: `${API_BASE_URL}/secondhand/products`,       // GET - 商品列表（真实接口）
        productBase: `${API_BASE_URL}/secondhand/products`,    // GET/PUT/PATCH/DELETE - 商品基础路径
        myProducts: `${API_BASE_URL}/secondhand/my-products`,  // GET - 我的发布
        statistics: `${API_BASE_URL}/secondhand/statistics`,   // GET - 我的统计
    },

    // 代课平台相关
    substitute: {
        list: `${API_BASE_URL}/substitute/tasks`,              // GET - 获取代课任务列表
        detailBase: `${API_BASE_URL}/substitute/tasks`,        // GET - 获取任务详情
        publish: `${API_BASE_URL}/substitute/publish`,         // POST - 发布代课需求
        taskBase: `${API_BASE_URL}/substitute/tasks`,          // POST - 任务操作基础路径
        myPublished: `${API_BASE_URL}/substitute/my-published`, // GET - 我发布的任务
        myAccepted: `${API_BASE_URL}/substitute/my-accepted`,  // GET - 我接受的任务
        statistics: `${API_BASE_URL}/substitute/statistics`,   // GET - 任务统计
    },

    // 站长好物相关
    recommend: {
        list: `${API_BASE_URL}/recommend/list`,                // GET - 获取推荐商品列表
        detail: `${API_BASE_URL}/recommend/detail`,            // GET - 获取商品详情
        getCoupon: `${API_BASE_URL}/recommend/coupon`,         // POST - 领取优惠券
        adminList: `${API_BASE_URL}/recommend/admin/list`,     // GET - 管理员查询好物
        adminCreate: `${API_BASE_URL}/recommend/admin/create`, // POST - 管理员上架好物
        adminStatusBase: `${API_BASE_URL}/recommend/admin`,    // POST - 管理员上下架
    },

    // 学习资源相关
    resources: {
        materials: {
            list: `${API_BASE_URL}/resources/materials/list`,      // GET - 获取学习资料列表
            download: `${API_BASE_URL}/resources/materials/download`, // GET - 下载资料
            upload: `${API_BASE_URL}/resources/materials/upload`,  // POST - 上传资料
        },
        software: {
            list: `${API_BASE_URL}/resources/software/list`,       // GET - 获取软件列表
            download: `${API_BASE_URL}/resources/software/download`, // GET - 下载软件
        },
    },

    // 联系作者相关
    contact: {
        submit: `${API_BASE_URL}/contact/submit`,              // POST - 提交联系表单
    },

    // 管理员后台专用接口（Java 后端）
    admin: {
        checkinPending: `${API_BASE_URL}/checkin/pending`,     // GET - 待审核签到记录
        checkinApproveBase: `${API_BASE_URL}/checkin/approve`, // POST - 审核签到
        checkinRecords: `${API_BASE_URL}/checkin/admin/records`, // GET - 管理员查询签到记录
        checkinForceStatusBase: `${API_BASE_URL}/checkin/admin/records`, // POST - 管理员强制签到状态
        secondhandProducts: `${API_BASE_URL}/secondhand/products`, // GET - 商品列表
        secondhandAdminProducts: `${API_BASE_URL}/secondhand/admin/products`, // GET - 管理员查询全量商品
        secondhandAdminStatusBase: `${API_BASE_URL}/secondhand/admin/products`, // POST - 管理员强制商品状态
        secondhandAdminDeleteBase: `${API_BASE_URL}/secondhand/admin/products`, // DELETE - 管理员删除商品
        substituteTasks: `${API_BASE_URL}/substitute/tasks`,   // GET - 任务列表
        substituteAdminTasks: `${API_BASE_URL}/substitute/admin/tasks`, // GET - 管理员查询全量任务
        substituteAdminStatusBase: `${API_BASE_URL}/substitute/admin/tasks`, // POST - 管理员强制任务状态
        pointsUsers: `${API_BASE_URL}/points/admin/users`,      // GET - 管理员查询用户列表
        pointsAdjust: `${API_BASE_URL}/points/admin/adjust`,    // POST - 管理员增减积分
        pointsUserStatusBase: `${API_BASE_URL}/points/admin/users`, // POST - 管理员启用/禁用用户
        pointsResetSigninBase: `${API_BASE_URL}/points/admin/users`, // POST - 管理员重置签到信息
        pointsHistory: `${API_BASE_URL}/points/admin/history`,  // GET - 管理员查看积分流水
        resourceUploadMaterial: `${API_BASE_URL}/resources/admin/materials/upload`, // POST - 管理员上传学习资料
        resourceUploadSoftware: `${API_BASE_URL}/resources/admin/software/upload`, // POST - 管理员上传学习软件
        recommendAdminList: `${API_BASE_URL}/recommend/admin/list`, // GET - 管理员查询好物
        recommendAdminCreate: `${API_BASE_URL}/recommend/admin/create`, // POST - 管理员上架好物
        recommendAdminStatusBase: `${API_BASE_URL}/recommend/admin`, // POST - 管理员上下架
        announcementAdminList: `${API_BASE_URL}/announcement/admin/list`, // GET - 管理员查询公告
        announcementPublish: `${API_BASE_URL}/announcement/admin/publish`, // POST - 管理员发布公告
    },
};

// 导出配置
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        API_BASE_URL,
        API_ENDPOINTS,
        API_CONFIG_STORAGE_KEY,
        normalizeApiBaseUrl,
        resolveApiBaseUrl,
        setApiBaseUrl,
    };
}
