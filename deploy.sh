#!/bin/bash
# ========================================
#  校园综合服务平台 - Docker 一键部署脚本
#  用法: bash deploy.sh [start|stop|restart|logs|status]
# ========================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 检查 .env 文件
check_env() {
    if [ ! -f .env ]; then
        warn ".env 文件不存在，正在从 .env.example 复制..."
        cp .env.example .env
        warn "请编辑 .env 文件修改默认密码后再部署！"
        warn "  vim .env"
        exit 1
    fi
}

# 启动
start() {
    check_env
    info "正在构建并启动所有服务..."
    docker compose up -d --build
    info "部署完成！"
    info "  前端地址: http://localhost:${FRONTEND_PORT:-80}"
    info "  后端 API: http://localhost:${BACKEND_PORT:-8080}/api"
    info "  管理后台: http://localhost:${FRONTEND_PORT:-80}/pages/admin-login.html"
    echo ""
    info "查看日志: bash deploy.sh logs"
    info "查看状态: bash deploy.sh status"
}

# 停止
stop() {
    info "正在停止所有服务..."
    docker compose down
    info "已停止"
}

# 重启
restart() {
    info "正在重启所有服务..."
    docker compose restart
    info "重启完成"
}

# 日志
logs() {
    docker compose logs -f --tail=100
}

# 状态
status() {
    docker compose ps
}

# 清理（删除数据卷）
clean() {
    warn "此操作将删除所有数据（包括数据库）！"
    read -p "确认删除？(y/N): " confirm
    if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
        docker compose down -v
        info "已清理所有服务和数据卷"
    else
        info "已取消"
    fi
}

# 主入口
case "${1}" in
    start)   start ;;
    stop)    stop ;;
    restart) restart ;;
    logs)    logs ;;
    status)  status ;;
    clean)   clean ;;
    *)
        echo "用法: bash deploy.sh {start|stop|restart|logs|status|clean}"
        echo ""
        echo "  start   - 构建并启动所有服务"
        echo "  stop    - 停止所有服务"
        echo "  restart - 重启所有服务"
        echo "  logs    - 查看实时日志"
        echo "  status  - 查看服务状态"
        echo "  clean   - 停止并删除所有数据（危险操作）"
        exit 1
        ;;
esac
