/**
 * 首页登录/注册交互逻辑
 */

(function initIndexPage() {
    const modalIds = ['loginModal', 'registerModal'];

    function getModal(modalId) {
        return document.getElementById(modalId);
    }

    function openModal(modalId) {
        const modal = getModal(modalId);
        if (!modal) {
            return;
        }
        modal.style.display = 'flex';
        modal.setAttribute('aria-hidden', 'false');
    }

    function closeModal(modalId) {
        const modal = getModal(modalId);
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
        document.addEventListener('click', (event) => {
            const closer = event.target.closest('[data-modal-close]');
            if (closer) {
                closeModal(closer.dataset.modalClose);
            }

            const trigger = event.target.closest('[data-modal-target]');
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

        document.addEventListener('keydown', (event) => {
            if (event.key === 'Escape') {
                closeAllModals();
            }
        });
    }

    function getAuthPayload(response) {
        return response && response.data ? response.data : response;
    }

    async function handleLogin(event) {
        event.preventDefault();

        const username = document.getElementById('login-username').value.trim();
        const password = document.getElementById('login-password').value;

        if (!username || !password) {
            showMessage('请填写完整的登录信息', 'warning');
            return;
        }

        try {
            showLoading('登录中...');
            const response = await post(API_ENDPOINTS.auth.login, {
                username,
                password,
            });
            hideLoading();

            const payload = getAuthPayload(response);
            if (!response.success || !payload) {
                showMessage(response.message || '登录失败', 'error');
                return;
            }

            setAuthSession(payload.token, payload.user);
            showMessage('登录成功！', 'success');
            closeModal('loginModal');

            setTimeout(() => {
                window.location.href = 'main.html';
            }, 600);
        } catch (error) {
            handleError(error, '登录失败，请检查用户名和密码');
        }
    }

    async function handleRegister(event) {
        event.preventDefault();

        const username = document.getElementById('reg-username').value.trim();
        const email = document.getElementById('reg-email').value.trim();
        const password = document.getElementById('reg-password').value;

        if (!username || !email || !password) {
            showMessage('请填写完整的注册信息', 'warning');
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
            const response = await post(API_ENDPOINTS.auth.register, {
                username,
                email,
                password,
            });
            hideLoading();

            if (!response.success) {
                showMessage(response.message || '注册失败', 'error');
                return;
            }

            showMessage('注册成功！请登录', 'success');
            closeModal('registerModal');
            document.getElementById('login-username').value = username;

            setTimeout(() => {
                openModal('loginModal');
            }, 600);
        } catch (error) {
            handleError(error, '注册失败，请稍后重试');
        }
    }

    function bindForms() {
        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');

        if (loginForm) {
            loginForm.addEventListener('submit', handleLogin);
        }
        if (registerForm) {
            registerForm.addEventListener('submit', handleRegister);
        }
    }

    function redirectLoggedInUser() {
        if (isLoggedIn()) {
            window.location.href = 'main.html';
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        bindModalEvents();
        bindForms();
        redirectLoggedInUser();
    });
})();
