"""
自动更新服务 — 轻量级 HTTP API
提供 git pull、docker compose 重建、版本查询等功能
"""

import os
import subprocess
import json
import time
from http.server import HTTPServer, BaseHTTPRequestHandler

UPDATE_SECRET = os.environ.get('UPDATE_SECRET', '')
PROJECT_DIR = os.environ.get('PROJECT_DIR', '/app/project')
COMPOSE_CMD = os.environ.get('COMPOSE_CMD', 'docker compose')

def run_cmd(cmd, cwd=None, timeout=120):
    """执行 shell 命令，返回 (returncode, stdout, stderr)"""
    try:
        result = subprocess.run(
            cmd, shell=True, cwd=cwd or PROJECT_DIR,
            capture_output=True, text=True, timeout=timeout
        )
        return result.returncode, result.stdout.strip(), result.stderr.strip()
    except subprocess.TimeoutExpired:
        return -1, '', '命令执行超时'
    except Exception as e:
        return -1, '', str(e)

def get_version_info():
    """获取当前版本信息"""
    info = {}

    # 当前 commit
    code, out, _ = run_cmd('git log --oneline -1')
    info['current'] = out if code == 0 else 'unknown'

    # 当前分支
    code, out, _ = run_cmd('git rev-parse --abbrev-ref HEAD')
    info['branch'] = out if code == 0 else 'unknown'

    # 最近 5 条记录
    code, out, _ = run_cmd('git log --oneline -5')
    info['history'] = out.split('\n') if code == 0 else []

    # 当前时间
    info['time'] = time.strftime('%Y-%m-%d %H:%M:%S')

    return info

def check_remote_updates():
    """检查远程是否有新版本"""
    # 先 fetch
    code, _, err = run_cmd('git fetch origin', timeout=30)
    if code != 0:
        return {'hasUpdate': False, 'error': 'fetch 失败: ' + err}

    # 比较本地和远程
    code, local_hash, _ = run_cmd('git rev-parse HEAD')
    code2, remote_hash, _ = run_cmd('git rev-parse origin/main')

    if code != 0 or code2 != 0:
        return {'hasUpdate': False, 'error': '无法获取版本信息'}

    local_hash = local_hash.strip()
    remote_hash = remote_hash.strip()

    if local_hash == remote_hash:
        return {'hasUpdate': False, 'message': '已是最新版本'}

    # 获取差异提交
    code, diff_log, _ = run_cmd(f'git log --oneline {local_hash}..{remote_hash}')

    return {
        'hasUpdate': True,
        'local': local_hash[:8],
        'remote': remote_hash[:8],
        'commits': diff_log.split('\n') if code == 0 else [],
    }

def do_pull():
    """执行 git pull"""
    code, out, err = run_cmd('git pull origin main', timeout=60)
    success = code == 0
    return {
        'success': success,
        'output': out,
        'error': err if not success else '',
    }

def do_deploy():
    """执行完整部署：git pull + docker compose build + restart"""
    steps = []

    # Step 1: git pull
    code, out, err = run_cmd('git pull origin main', timeout=60)
    steps.append({'step': 'git pull', 'success': code == 0, 'output': out, 'error': err})
    if code != 0:
        return {'success': False, 'steps': steps, 'message': 'git pull 失败'}

    # Step 2: build frontend
    code, out, err = run_cmd(f'{COMPOSE_CMD} build frontend', timeout=180)
    steps.append({'step': 'build frontend', 'success': code == 0, 'output': out, 'error': err})
    if code != 0:
        return {'success': False, 'steps': steps, 'message': '构建前端镜像失败'}

    # Step 3: restart frontend
    code, out, err = run_cmd(f'{COMPOSE_CMD} up -d frontend', timeout=60)
    steps.append({'step': 'restart frontend', 'success': code == 0, 'output': out, 'error': err})
    if code != 0:
        return {'success': False, 'steps': steps, 'message': '重启前端容器失败'}

    return {'success': True, 'steps': steps, 'message': '更新完成'}

def verify_token(headers):
    """验证 Bearer Token"""
    if not UPDATE_SECRET:
        return True  # 未设置密钥则跳过验证（仅限内部网络）
    auth = headers.get('Authorization', '')
    return auth == f'Bearer {UPDATE_SECRET}'

class UpdateHandler(BaseHTTPRequestHandler):
    def log_message(self, format, *args):
        pass  # 禁用默认日志

    def send_json(self, data, status=200):
        body = json.dumps(data, ensure_ascii=False).encode('utf-8')
        self.send_response(status)
        self.send_header('Content-Type', 'application/json; charset=utf-8')
        self.send_header('Content-Length', len(body))
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        if not verify_token(self.headers):
            self.send_json({'error': '未授权'}, 401)
            return

        if self.path == '/api/update/status':
            self.send_json(get_version_info())
        elif self.path == '/api/update/check':
            self.send_json(check_remote_updates())
        else:
            self.send_json({'error': '未知接口'}, 404)

    def do_POST(self):
        if not verify_token(self.headers):
            self.send_json({'error': '未授权'}, 401)
            return

        if self.path == '/api/update/pull':
            self.send_json(do_pull())
        elif self.path == '/api/update/deploy':
            self.send_json(do_deploy())
        else:
            self.send_json({'error': '未知接口'}, 404)

def main():
    port = int(os.environ.get('UPDATER_PORT', '8081'))
    server = HTTPServer(('0.0.0.0', port), UpdateHandler)
    print(f'更新服务已启动，监听端口 {port}')
    server.serve_forever()

if __name__ == '__main__':
    main()
