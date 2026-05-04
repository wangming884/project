/**
 * API 配置文件
 * 统一管理所有后端接口地址
 */

// API 基础地址配置
const API_BASE_URL = window.location.hostname === 'localhost' 
    ? 'http://localhost:3000/api'  // 本地开发环境
    : '/api';  // 生产环境

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
        getHistory: `${API_BASE_URL}/points/history`,          // GET - 积分历史记录
        purchase: `${API_BASE_URL}/points/purchase`,           // POST - 购买积分
    },

    // 晚寝签到相关
    checkIn: {
        submit: `${API_BASE_URL}/checkin/submit`,              // POST - 提交晚寝签到
        getStatus: `${API_BASE_URL}/checkin/status`,           // GET - 获取签到状态
        getHistory: `${API_BASE_URL}/checkin/history`,         // GET - 签到历史记录
        getLocation: `${API_BASE_URL}/checkin/location`,       // GET - 获取定位信息
    },

    // 二手物品交易相关
    secondhand: {
        list: `${API_BASE_URL}/secondhand/list`,               // GET - 获取商品列表
        detail: `${API_BASE_URL}/secondhand/detail`,           // GET - 获取商品详情
        publish: `${API_BASE_URL}/secondhand/publish`,         // POST - 发布商品
        update: `${API_BASE_URL}/secondhand/update`,           // PUT - 更新商品
        delete: `${API_BASE_URL}/secondhand/delete`,           // DELETE - 删除商品
        search: `${API_BASE_URL}/secondhand/search`,           // GET - 搜索商品
        contact: `${API_BASE_URL}/secondhand/contact`,         // POST - 联系卖家
    },

    // 代课平台相关
    substitute: {
        list: `${API_BASE_URL}/substitute/list`,               // GET - 获取代课任务列表
        detail: `${API_BASE_URL}/substitute/detail`,           // GET - 获取任务详情
        publish: `${API_BASE_URL}/substitute/publish`,         // POST - 发布代课需求
        accept: `${API_BASE_URL}/substitute/accept`,           // POST - 接受代课任务
        complete: `${API_BASE_URL}/substitute/complete`,       // POST - 完成任务
        cancel: `${API_BASE_URL}/substitute/cancel`,           // POST - 取消任务
    },

    // 站长好物相关
    recommend: {
        list: `${API_BASE_URL}/recommend/list`,                // GET - 获取推荐商品列表
        detail: `${API_BASE_URL}/recommend/detail`,            // GET - 获取商品详情
        getCoupon: `${API_BASE_URL}/recommend/coupon`,         // POST - 领取优惠券
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
        secondhandProducts: `${API_BASE_URL}/secondhand/products`, // GET - 商品列表
        substituteTasks: `${API_BASE_URL}/substitute/tasks`,   // GET - 任务列表
    },
};

// 导出配置
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { API_BASE_URL, API_ENDPOINTS };
}
