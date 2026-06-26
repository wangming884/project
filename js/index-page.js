/**
 * 首页登录/注册交互逻辑
 */

(function initIndexPage() {
    var modalIds = ['loginModal', 'registerModal'];

    // 验证码实例
    var loginCaptcha = null;
    var registerCaptcha = null;
    var LOCAL_ADMIN = {
        username: 'admin',
        password: 'Admin@123456',
    };

    function getModal(modalId) {
        return document.getElementById(modalId);
    }

    function openModal(modalId) {
        var modal = getModal(modalId);
        if (!modal) {
            return;
        }
        modal.style.display = 'flex';
        modal.setAttribute('aria-hidden', 'false');
        // 打开弹窗时刷新验证码
        if (modalId === 'loginModal' && loginCaptcha) {
            loginCaptcha.refresh();
        }
        if (modalId === 'registerModal' && registerCaptcha) {
            registerCaptcha.refresh();
        }
    }

    function closeModal(modalId) {
        var modal = getModal(modalId);
        if (!modal) {
            return;
        }
        modal.style.display = 'none';
        modal.setAttribute('aria-hidden', 'true');
    }

    function closeAllModals() {
        modalIds.forEach(closeModal);
    }

    function bindModalEvents() {
        document.addEventListener('click', function (event) {
            var closer = event.target.closest('[data-modal-close]');
            if (closer) {
                closeModal(closer.dataset.modalClose);
            }

            var trigger = event.target.closest('[data-modal-target]');
            if (trigger) {
                closeAllModals();
                openModal(trigger.dataset.modalTarget);
                return;
            }

            if (closer) {
                return;
            }

            if (event.target.classList.contains('modal')) {
                closeModal(event.target.id);
            }
        });

        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') {
                closeAllModals();
            }
        });
    }

    function getAuthPayload(response) {
        return response && response.data ? response.data : response;
    }

    function isAdminUser(user) {
        return !!user && (user.role === 'admin' || user.userId === 0);
    }

    function getPagePath(page) {
        if (!window.location.pathname.includes('/pages/')) {
            return 'pages/' + page;
        }
        return page;
    }

    function saveAdminLoginSession(token, user, mode) {
        setAuthSession(token, user);

        if (typeof saveAdminSession === 'function') {
            saveAdminSession({
                token: token,
                username: user.username,
                userId: user.userId,
                role: user.role,
                permissions: user.permissions,
                mode: mode || 'server',
            });
        }
    }

    function redirectAfterLogin(isAdmin) {
        window.location.href = getPagePath(isAdmin ? 'admin-dashboard.html' : 'main.html');
    }

    async function handleLogin(event) {
        event.preventDefault();

        var username = document.getElementById('login-username').value.trim();
        var password = document.getElementById('login-password').value;

        if (!username || !password) {
            showMessage('请填写完整的登录信息', 'warning');
            return;
        }

        // 验证码校验
        var captchaInput = document.getElementById('login-captcha').value.trim();
        if (!captchaInput) {
            showMessage('请输入验证码', 'warning');
            return;
        }
        if (!loginCaptcha.verify(captchaInput)) {
            showMessage('验证码错误，请重新输入', 'warning');
            loginCaptcha.refresh();
            document.getElementById('login-captcha').value = '';
            return;
        }

        try {
            showLoading('登录中...');
            var response = await post(API_ENDPOINTS.auth.login, {
                username: username,
                password: password,
            });
            hideLoading();

            var payload = getAuthPayload(response);
            if (!response.success || !payload) {
                showMessage('账号或密码错误', 'error');
                loginCaptcha.refresh();
                document.getElementById('login-captcha').value = '';
                return;
            }

            var isAdmin = isAdminUser(payload.user);
            if (isAdmin) {
                saveAdminLoginSession(payload.token, payload.user, 'server');
            } else {
                setAuthSession(payload.token, payload.user);
            }

            showMessage('登录成功！', 'success');
            closeModal('loginModal');

            setTimeout(function () {
                redirectAfterLogin(isAdmin);
            }, 600);
        } catch (error) {
            hideLoading();
            loginCaptcha.refresh();
            document.getElementById('login-captcha').value = '';

            if (
                isOfflineFallbackError(error)
                && username === LOCAL_ADMIN.username
                && password === LOCAL_ADMIN.password
            ) {
                saveAdminLoginSession('', {
                    userId: 0,
                    username: username,
                    role: 'admin',
                    permissions: 'all',
                }, 'offline');
                showMessage('后端不可用，已进入离线管理模式', 'warning');
                closeModal('loginModal');
                setTimeout(function () {
                    redirectAfterLogin(true);
                }, 700);
                return;
            }

            var msg = (error && error.message) ? error.message : '登录失败，请稍后重试';
            showMessage(msg, 'error');
            console.error('登录异常:', error);
        }
    }

    async function handleRegister(event) {
        event.preventDefault();

        var username = document.getElementById('reg-username').value.trim();
        var email = document.getElementById('reg-email').value.trim();
        var password = document.getElementById('reg-password').value;

        if (!username || !email || !password) {
            showMessage('请填写完整的注册信息', 'warning');
            return;
        }

        // 验证码校验
        var captchaInput = document.getElementById('reg-captcha').value.trim();
        if (!captchaInput) {
            showMessage('请输入验证码', 'warning');
            return;
        }
        if (!registerCaptcha.verify(captchaInput)) {
            showMessage('验证码错误，请重新输入', 'warning');
            registerCaptcha.refresh();
            document.getElementById('reg-captcha').value = '';
            return;
        }

        if (!validateEmail(email)) {
            showMessage('请输入有效的邮箱地址', 'warning');
            return;
        }

        if (!validatePassword(password)) {
            showMessage('密码长度至少为6位', 'warning');
            return;
        }

        try {
            showLoading('注册中...');
            var response = await post(API_ENDPOINTS.auth.register, {
                username: username,
                email: email,
                password: password,
            });
            hideLoading();

            if (!response.success) {
                showMessage(response.message || '注册失败', 'error');
                registerCaptcha.refresh();
                document.getElementById('reg-captcha').value = '';
                return;
            }

            showMessage('注册成功！请登录', 'success');
            closeModal('registerModal');
            document.getElementById('login-username').value = username;

            setTimeout(function () {
                openModal('loginModal');
            }, 600);
        } catch (error) {
            hideLoading();
            registerCaptcha.refresh();
            document.getElementById('reg-captcha').value = '';
            handleError(error, '注册失败，请稍后重试');
        }
    }

    function bindForms() {
        var loginForm = document.getElementById('loginForm');
        var registerForm = document.getElementById('registerForm');

        if (loginForm) {
            loginForm.addEventListener('submit', handleLogin);
        }
        if (registerForm) {
            registerForm.addEventListener('submit', handleRegister);
        }
    }

    function initCaptchas() {
        if (typeof CaptchaGenerator !== 'undefined') {
            loginCaptcha = CaptchaGenerator.create('login-captcha-canvas');
            registerCaptcha = CaptchaGenerator.create('reg-captcha-canvas');

            // 点击 canvas 刷新验证码
            var loginCanvas = document.getElementById('login-captcha-canvas');
            var regCanvas = document.getElementById('reg-captcha-canvas');
            if (loginCanvas) {
                loginCanvas.addEventListener('click', function () {
                    loginCaptcha.refresh();
                    document.getElementById('login-captcha').value = '';
                });
            }
            if (regCanvas) {
                regCanvas.addEventListener('click', function () {
                    registerCaptcha.refresh();
                    document.getElementById('reg-captcha').value = '';
                });
            }
        }
    }

    function redirectLoggedInUser() {
        if (isLoggedIn()) {
            var user = getUserInfo();
            if (isAdminUser(user)) {
                if (typeof isAdminLoggedIn === 'function' && isAdminLoggedIn()) {
                    redirectAfterLogin(true);
                } else {
                    clearAuthSession();
                }
                return;
            }

            redirectAfterLogin(false);
            return;
        }

        if (typeof isAdminLoggedIn === 'function' && isAdminLoggedIn()) {
            redirectAfterLogin(true);
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        bindModalEvents();
        bindForms();
        initCaptchas();
        redirectLoggedInUser();
    });
})();
