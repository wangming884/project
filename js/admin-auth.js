/**
 * 管理员认证工具
 */

const ADMIN_SESSION_KEY = 'adminSession';
const ADMIN_SESSION_EXPIRES_HOURS = 8;

function saveAdminSession(adminInfo) {
    const expiresAt = Date.now() + ADMIN_SESSION_EXPIRES_HOURS * 60 * 60 * 1000;
    setStorage(ADMIN_SESSION_KEY, {
        ...adminInfo,
        expiresAt,
    });
}

function getAdminSession() {
    const session = getStorage(ADMIN_SESSION_KEY);
    if (!session || !session.expiresAt) {
        return null;
    }
    if (Date.now() > Number(session.expiresAt)) {
        removeStorage(ADMIN_SESSION_KEY);
        return null;
    }
    return session;
}

function isAdminLoggedIn() {
    return !!getAdminSession();
}

function hasAdminPermission(permission = 'all') {
    const session = getAdminSession();
    if (!session) {
        return false;
    }
    if (session.permissions === 'all') {
        return true;
    }
    if (Array.isArray(session.permissions)) {
        return session.permissions.includes(permission);
    }
    return session.permissions === permission;
}

function requireAdminAuth() {
    const session = getAdminSession();
    if (!session) {
        window.location.href = 'index.html';
        return false;
    }
    return true;
}

function adminLogout() {
    removeStorage(ADMIN_SESSION_KEY);
    clearAuthSession();
    window.location.href = 'index.html';
}
