/**
 * API 工具函数库
 * 提供统一的HTTP请求封装、错误处理、加载状态管理等功能
 */

const AUTH_STORAGE_KEYS = {
    token: 'authToken',
    user: 'userInfo',
};

// ==================== HTTP 请求封装 ====================

function isPlainObject(value) {
    return Object.prototype.toString.call(value) === '[object Object]';
}

function createRequestHeaders(headers = {}) {
    if (headers instanceof Headers) {
        return headers;
    }
    return new Headers(headers);
}

function shouldSerializeJsonBody(body) {
    return isPlainObject(body) || Array.isArray(body);
}

async function parseResponseBody(response) {
    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
        return response.json();
    }

    const text = await response.text();
    return text ? { message: text } : null;
}

/**
 * 统一的 HTTP 请求函数
 * @param {string} url - 请求地址
 * @param {object} options - 请求配置
 * @returns {Promise} 返回响应数据
 */
async function request(url, options = {}) {
    const defaultOptions = {
        method: 'GET',
        credentials: 'include', // 携带 cookie
    };

    // 合并配置
    const config = { ...defaultOptions, ...options };

    config.headers = createRequestHeaders(options.headers);

    // 如果有 body 且是普通对象，转换为 JSON；FormData 则保持原样
    if (config.body && shouldSerializeJsonBody(config.body)) {
        if (!config.headers.has('Content-Type')) {
            config.headers.set('Content-Type', 'application/json');
        }
        config.body = JSON.stringify(config.body);
    }

    const token = getAuthToken();
    if (token) {
        config.headers.set('Authorization', `Bearer ${token}`);
    }

    try {
        const response = await fetch(url, config);
        const responseBody = await parseResponseBody(response);

        // 处理 HTTP 错误状态
        if (!response.ok) {
            const message = responseBody && responseBody.message
                ? responseBody.message
                : `HTTP Error: ${response.status}`;
            const error = new Error(message);
            error.status = response.status;
            error.response = responseBody;
            throw error;
        }

        // 解析响应数据
        return responseBody;
    } catch (error) {
        console.error('API Request Error:', error);
        throw error;
    }
}

/**
 * GET 请求
 */
async function get(url, params = {}) {
    // 构建查询参数
    const queryString = new URLSearchParams(params).toString();
    const fullUrl = queryString ? `${url}?${queryString}` : url;
    
    return request(fullUrl, { method: 'GET' });
}

/**
 * POST 请求
 */
async function post(url, data = {}) {
    return request(url, {
        method: 'POST',
        body: data,
    });
}

/**
 * PUT 请求
 */
async function put(url, data = {}) {
    return request(url, {
        method: 'PUT',
        body: data,
    });
}

/**
 * DELETE 请求
 */
async function del(url, data = {}) {
    return request(url, {
        method: 'DELETE',
        body: data,
    });
}

// ==================== 加载状态管理 ====================

/**
 * 显示加载动画
 * @param {string} message - 加载提示文字
 */
function showLoading(message = '加载中...') {
    // 移除已存在的加载层
    hideLoading();
    
    const loadingDiv = document.createElement('div');
    loadingDiv.id = 'global-loading';
    loadingDiv.innerHTML = `
        <div style="
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        ">
            <div style="
                background: white;
                padding: 2rem 3rem;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.2);
                text-align: center;
            ">
                <div style="
                    width: 40px;
                    height: 40px;
                    border: 4px solid #f3f3f3;
                    border-top: 4px solid #3182ce;
                    border-radius: 50%;
                    animation: spin 1s linear infinite;
                    margin: 0 auto 1rem;
                "></div>
                <div style="color: #333; font-size: 1rem;">${message}</div>
            </div>
        </div>
        <style>
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        </style>
    `;
    document.body.appendChild(loadingDiv);
}

/**
 * 隐藏加载动画
 */
function hideLoading() {
    const loadingDiv = document.getElementById('global-loading');
    if (loadingDiv) {
        loadingDiv.remove();
    }
}

// ==================== 消息提示 ====================

/**
 * 显示提示消息
 * @param {string} message - 提示内容
 * @param {string} type - 提示类型: success, error, warning, info
 * @param {number} duration - 显示时长（毫秒）
 */
function showMessage(message, type = 'info', duration = 3000) {
    const colors = {
        success: '#38b2ac',
        error: '#e53e3e',
        warning: '#ed8936',
        info: '#3182ce',
    };

    const icons = {
        success: '✓',
        error: '✕',
        warning: '⚠',
        info: 'ℹ',
    };

    const messageDiv = document.createElement('div');
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        background: ${colors[type] || colors.info};
        color: white;
        padding: 1rem 2rem;
        border-radius: 8px;
        box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        z-index: 10000;
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 1rem;
        animation: slideDown 0.3s ease-out;
    `;
    messageDiv.innerHTML = `
        <span style="font-size: 1.2rem; font-weight: bold;">${icons[type] || icons.info}</span>
        <span>${message}</span>
    `;

    // 添加动画样式
    if (!document.getElementById('message-animation-style')) {
        const style = document.createElement('style');
        style.id = 'message-animation-style';
        style.textContent = `
            @keyframes slideDown {
                from { opacity: 0; transform: translateX(-50%) translateY(-20px); }
                to { opacity: 1; transform: translateX(-50%) translateY(0); }
            }
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(messageDiv);

    // 自动移除
    setTimeout(() => {
        messageDiv.style.animation = 'slideDown 0.3s ease-out reverse';
        setTimeout(() => messageDiv.remove(), 300);
    }, duration);
}

// ==================== 错误处理 ====================

/**
 * 统一错误处理函数
 * @param {Error} error - 错误对象
 * @param {string} defaultMessage - 默认错误提示
 */
function handleError(error, defaultMessage = '操作失败，请稍后重试') {
    console.error('Error:', error);
    
    let message = defaultMessage;
    
    // 解析错误信息
    if (error.message) {
        message = error.message;
    }
    
    // 特殊错误处理
    if (message.includes('401') || message.includes('Unauthorized')) {
        message = '登录已过期，请重新登录';
        // 清除登录信息
        clearAuthSession();
        // 跳转到登录页
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1500);
    } else if (message.includes('403') || message.includes('Forbidden')) {
        message = '没有权限执行此操作';
    } else if (message.includes('404') || message.includes('Not Found')) {
        message = '请求的资源不存在';
    } else if (message.includes('500') || message.includes('Server Error')) {
        message = '服务器错误，请稍后重试';
    } else if (message.includes('Network') || message.includes('Failed to fetch')) {
        message = '网络连接失败，请检查网络';
    }
    
    showMessage(message, 'error');
    hideLoading();
}

// ==================== 本地存储工具 ====================

/**
 * 存储数据到 localStorage（支持对象）
 */
function setStorage(key, value) {
    try {
        const data = typeof value === 'object' ? JSON.stringify(value) : value;
        localStorage.setItem(key, data);
    } catch (error) {
        console.error('Storage Error:', error);
    }
}

/**
 * 从 localStorage 获取数据（自动解析 JSON）
 */
function getStorage(key, defaultValue = null) {
    try {
        const data = localStorage.getItem(key);
        if (data === null) return defaultValue;
        
        // 尝试解析 JSON
        try {
            return JSON.parse(data);
        } catch {
            return data;
        }
    } catch (error) {
        console.error('Storage Error:', error);
        return defaultValue;
    }
}

/**
 * 从 localStorage 删除数据
 */
function removeStorage(key) {
    try {
        localStorage.removeItem(key);
    } catch (error) {
        console.error('Storage Error:', error);
    }
}

/**
 * 清空 localStorage
 */
function clearStorage() {
    try {
        localStorage.clear();
    } catch (error) {
        console.error('Storage Error:', error);
    }
}

// ==================== 表单验证工具 ====================

/**
 * 验证邮箱格式
 */
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

/**
 * 验证手机号格式
 */
function validatePhone(phone) {
    const re = /^1[3-9]\d{9}$/;
    return re.test(phone);
}

/**
 * 验证密码强度（至少6位）
 */
function validatePassword(password) {
    return password && password.length >= 6;
}

// ==================== 日期时间工具 ====================

/**
 * 格式化日期时间
 * @param {Date|string|number} date - 日期对象、时间戳或日期字符串
 * @param {string} format - 格式化模板，默认 'YYYY-MM-DD HH:mm:ss'
 */
function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
    const d = new Date(date);
    
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    const seconds = String(d.getSeconds()).padStart(2, '0');
    
    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hours)
        .replace('mm', minutes)
        .replace('ss', seconds);
}

/**
 * 获取相对时间描述（如：刚刚、5分钟前、2小时前）
 */
function getRelativeTime(date) {
    const now = new Date();
    const past = new Date(date);
    const diff = now - past;
    
    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    
    if (seconds < 60) return '刚刚';
    if (minutes < 60) return `${minutes}分钟前`;
    if (hours < 24) return `${hours}小时前`;
    if (days < 7) return `${days}天前`;
    
    return formatDate(date, 'YYYY-MM-DD');
}

// ==================== 登录态工具 ====================

function getAuthToken() {
    return getStorage(AUTH_STORAGE_KEYS.token, '');
}

function getUserInfo() {
    return getStorage(AUTH_STORAGE_KEYS.user);
}

function setAuthSession(token, user) {
    if (token) {
        setStorage(AUTH_STORAGE_KEYS.token, token);
    }
    if (user) {
        setStorage(AUTH_STORAGE_KEYS.user, user);
    }
}

function clearAuthSession() {
    removeStorage(AUTH_STORAGE_KEYS.token);
    removeStorage(AUTH_STORAGE_KEYS.user);
}

function isLoggedIn() {
    return !!getAuthToken();
}

function isOfflineFallbackError(error) {
    if (error && Number.isFinite(Number(error.status)) && Number(error.status) > 0) {
        return false;
    }

    const message = String(error && error.message ? error.message : '');
    return !message
        || message.includes('Network')
        || message.includes('Failed to fetch')
        || message.includes('Load failed');
}

// ==================== 导出所有工具函数 ====================

if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        AUTH_STORAGE_KEYS,
        request, get, post, put, del,
        showLoading, hideLoading,
        showMessage, handleError,
        setStorage, getStorage, removeStorage, clearStorage,
        validateEmail, validatePhone, validatePassword,
        formatDate, getRelativeTime,
        getAuthToken, getUserInfo, setAuthSession, clearAuthSession, isLoggedIn,
        isOfflineFallbackError,
    };
}
