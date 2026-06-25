/**
 * 管理后台公共工具函数
 * 所有管理子页面共用的辅助方法和 UI 组件
 */

// ==================== 数据处理辅助 ====================

function renderText(value, fallback) {
    fallback = fallback || '-';
    if (value === undefined || value === null || value === '') {
        return escapeHtml(fallback);
    }
    return escapeHtml(value);
}

function extractRows(payload) {
    if (!payload) return [];
    if (Array.isArray(payload)) return payload;
    if (Array.isArray(payload.records)) return payload.records;
    if (Array.isArray(payload.list)) return payload.list;
    if (payload.data) return extractRows(payload.data);
    return [];
}

function extractTotal(payload, rows) {
    if (!payload) return rows.length;
    if (typeof payload.total === 'number') return payload.total;
    if (payload.data) return extractTotal(payload.data, rows);
    return rows.length;
}

// ==================== 管理员身份 ====================

function initAdminIdentity(elementId) {
    elementId = elementId || 'adminIdentity';
    var el = document.getElementById(elementId);
    if (!el) return;
    var session = getAdminSession();
    var modeText = session && session.mode === 'offline' ? '离线模式' : '服务端模式';
    el.textContent = '当前管理员：' + (session && session.username ? session.username : '未知') + ' · ' + modeText;
}

// ==================== 通用页面样式 ====================

function getAdminPageStyles() {
    return `
        :root {
            --bg: #f3f6fb;
            --surface: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;
            --brand: #2563eb;
            --success: #10b981;
            --danger: #ef4444;
            --border: #e5e7eb;
        }
        * { box-sizing: border-box; }
        body { margin: 0; padding: 0; background: var(--bg); color: var(--text); font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
        header { background: var(--surface); border-bottom: 1px solid var(--border); position: sticky; top: 0; z-index: 20; }
        .topbar { max-width: 1200px; margin: 0 auto; padding: 0.95rem 5%; display: flex; justify-content: space-between; align-items: center; gap: 1rem; }
        .topbar h1 { margin: 0; font-size: 1.2rem; }
        .topbar small { color: var(--muted); display: block; margin-top: 0.15rem; }
        .top-actions { display: flex; gap: 0.6rem; flex-wrap: wrap; }
        .btn { border: none; border-radius: 10px; cursor: pointer; padding: 0.55rem 0.95rem; font-weight: 600; font-size: 0.88rem; text-decoration: none; display: inline-flex; align-items: center; }
        .btn-primary { background: var(--brand); color: #fff; }
        .btn-muted { background: #eef2ff; color: #3730a3; }
        .btn-danger { background: #fee2e2; color: #b91c1c; }
        .container { max-width: 1200px; margin: 1.2rem auto; padding: 0 5% 2rem; display: grid; gap: 1rem; }
        .panel { background: var(--surface); border: 1px solid var(--border); border-radius: 14px; padding: 1rem; }
        .panel h2 { margin: 0 0 0.7rem; font-size: 1rem; }
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; min-width: 640px; }
        th, td { text-align: left; border-bottom: 1px solid var(--border); padding: 0.65rem 0.4rem; font-size: 0.88rem; vertical-align: top; }
        th { color: var(--muted); font-weight: 600; }
        .ops { display: flex; gap: 0.4rem; flex-wrap: wrap; }
        .btn-ok, .btn-no { border: none; cursor: pointer; border-radius: 8px; padding: 0.35rem 0.55rem; font-size: 0.76rem; font-weight: 600; }
        .btn-ok { background: #dcfce7; color: #166534; }
        .btn-no { background: #fee2e2; color: #991b1b; }
        .btn-pick { border: none; cursor: pointer; border-radius: 8px; padding: 0.35rem 0.55rem; font-size: 0.76rem; font-weight: 600; background: #dbeafe; color: #1d4ed8; }
        .btn-ban { border: none; cursor: pointer; border-radius: 8px; padding: 0.35rem 0.55rem; font-size: 0.76rem; font-weight: 600; background: #fee2e2; color: #991b1b; }
        .btn-enable { border: none; cursor: pointer; border-radius: 8px; padding: 0.35rem 0.55rem; font-size: 0.76rem; font-weight: 600; background: #dcfce7; color: #166534; }
        .status-pill { display: inline-block; font-size: 0.75rem; padding: 0.1rem 0.45rem; border-radius: 999px; font-weight: 700; }
        .status-enabled { background: #dcfce7; color: #166534; }
        .status-disabled { background: #fee2e2; color: #991b1b; }
        .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 0.6rem; }
        .form-group { display: flex; flex-direction: column; gap: 0.3rem; margin-bottom: 0.7rem; }
        .form-group label { font-size: 0.82rem; color: var(--muted); }
        .form-group input { border: 1px solid var(--border); border-radius: 8px; padding: 0.55rem; font-size: 0.88rem; }
        .form-group select { border: 1px solid var(--border); border-radius: 8px; padding: 0.55rem; font-size: 0.88rem; background: #fff; }
        .form-group textarea { border: 1px solid var(--border); border-radius: 8px; padding: 0.55rem; font-size: 0.88rem; background: #fff; min-height: 96px; resize: vertical; font-family: inherit; }
        .form-group input:focus { outline: none; border-color: #93c5fd; box-shadow: 0 0 0 2px rgba(147,197,253,0.25); }
        .form-group select:focus { outline: none; border-color: #93c5fd; box-shadow: 0 0 0 2px rgba(147,197,253,0.25); }
        .form-group textarea:focus { outline: none; border-color: #93c5fd; box-shadow: 0 0 0 2px rgba(147,197,253,0.25); }
        .adjust-actions { display: flex; gap: 0.5rem; flex-wrap: wrap; margin-top: 0.4rem; }
        .empty { color: var(--muted); font-size: 0.9rem; padding: 0.5rem 0; }
        @media (max-width: 980px) {
            .form-row { grid-template-columns: 1fr; }
        }
    `;
}

function renderAdminTopbar(title, subtitle) {
    return `
    <header>
        <div class="topbar">
            <div>
                <h1>${escapeHtml(title)}</h1>
                <small id="adminIdentity">正在校验管理员身份...</small>
            </div>
            <div class="top-actions">
                <a class="btn btn-muted" href="admin-dashboard.html">返回后台</a>
                <a class="btn btn-muted" href="main.html">前台大厅</a>
                <button class="btn btn-danger" type="button" id="logoutBtn">退出管理员</button>
            </div>
        </div>
    </header>
    `;
}

function initAdminPage(title) {
    if (typeof requireAdminAuth === 'function' && !requireAdminAuth()) {
        return false;
    }
    // 注入样式
    var style = document.createElement('style');
    style.textContent = getAdminPageStyles();
    document.head.appendChild(style);
    // 注入顶栏
    var container = document.getElementById('admin-app');
    if (container) {
        container.insertAdjacentHTML('afterbegin', renderAdminTopbar(title));
    }
    // 初始化身份显示
    initAdminIdentity();
    // 绑定退出按钮
    var logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', adminLogout);
    }
    return true;
}
