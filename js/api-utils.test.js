const test = require('node:test');
const assert = require('node:assert/strict');

global.localStorage = {
    store: new Map(),
    getItem(key) {
        return this.store.has(key) ? this.store.get(key) : null;
    },
    setItem(key, value) {
        this.store.set(key, String(value));
    },
    removeItem(key) {
        this.store.delete(key);
    },
    clear() {
        this.store.clear();
    },
};

const {
    request,
    isOfflineFallbackError,
    sanitizeUrl,
    REQUEST_TIMEOUT_ERROR_CODE,
} = require('./api-utils.js');

async function withMutedConsoleError(run) {
    const originalConsoleError = console.error;
    console.error = () => {};
    try {
        return await run();
    } finally {
        console.error = originalConsoleError;
    }
}

test('request returns parsed JSON body for successful responses', async () => {
    let capturedOptions = null;

    global.fetch = async (url, options) => {
        capturedOptions = options;
        return new Response(JSON.stringify({
            success: true,
            message: 'ok',
            data: { id: 1 },
            code: 200,
        }), {
            status: 200,
            headers: { 'content-type': 'application/json' },
        });
    };

    const result = await request('https://example.com/api/demo', {
        method: 'POST',
        body: { name: 'campus' },
    });

    assert.equal(result.success, true);
    assert.deepEqual(result.data, { id: 1 });
    assert.equal(capturedOptions.method, 'POST');
    assert.equal(capturedOptions.headers.get('Content-Type'), 'application/json');
    assert.equal(capturedOptions.body, JSON.stringify({ name: 'campus' }));
});

test('request throws HTTP errors with response status', async () => {
    global.fetch = async () => new Response(JSON.stringify({
        message: '未登录',
        code: 401,
    }), {
        status: 401,
        headers: { 'content-type': 'application/json' },
    });

    await withMutedConsoleError(() => assert.rejects(
        () => request('https://example.com/api/protected'),
        (error) => {
            assert.equal(error.message, '未登录');
            assert.equal(error.status, 401);
            assert.equal(error.code, 401);
            assert.equal(isOfflineFallbackError(error), false);
            return true;
        }
    ));
});

test('request throws business errors for 200 plus success false envelopes', async () => {
    global.fetch = async () => new Response(JSON.stringify({
        success: false,
        message: '无管理员权限',
        code: 403,
        data: null,
    }), {
        status: 200,
        headers: { 'content-type': 'application/json' },
    });

    await withMutedConsoleError(() => assert.rejects(
        () => request('https://example.com/api/admin'),
        (error) => {
            assert.equal(error.message, '无管理员权限');
            assert.equal(error.status, 403);
            assert.equal(error.code, 403);
            assert.equal(error.response.success, false);
            assert.equal(isOfflineFallbackError(error), false);
            return true;
        }
    ));
});

test('isOfflineFallbackError only matches true connectivity failures', () => {
    assert.equal(isOfflineFallbackError(new Error('Failed to fetch')), true);
    assert.equal(isOfflineFallbackError({ message: 'Network Error' }), true);
    assert.equal(isOfflineFallbackError({ message: '无管理员权限', status: 403 }), false);
});

test('request converts timeout aborts into API errors', async () => {
    global.fetch = async (url, options) => new Promise((resolve, reject) => {
        options.signal.addEventListener('abort', () => {
            const abortError = new Error('aborted');
            abortError.name = 'AbortError';
            reject(abortError);
        }, { once: true });
    });

    await withMutedConsoleError(() => assert.rejects(
        () => request('https://example.com/api/slow', { timeoutMs: 5 }),
        (error) => {
            assert.equal(error.message, '请求超时，请稍后重试');
            assert.equal(error.status, 408);
            assert.equal(error.code, REQUEST_TIMEOUT_ERROR_CODE);
            return true;
        }
    ));
});

test('sanitizeUrl blocks unsupported protocols and keeps safe addresses', () => {
    assert.equal(sanitizeUrl('javascript:alert(1)'), '#');
    assert.equal(sanitizeUrl('https://example.com/demo?q=1'), 'https://example.com/demo?q=1');
    assert.equal(sanitizeUrl('/images/demo.png', { allowRelative: true }), '/images/demo.png');
});
