@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: ========================================
::  校园综合服务平台 - Docker 一键部署脚本 (Windows)
::  用法: deploy.bat [start|stop|restart|logs|status]
:: ========================================

cd /d "%~dp0"

if "%1"=="" goto usage

if "%1"=="start" goto start
if "%1"=="stop" goto stop
if "%1"=="restart" goto restart
if "%1"=="logs" goto logs
if "%1"=="status" goto status
if "%1"=="clean" goto clean
goto usage

:start
if not exist ".env" (
    echo [WARN] .env 文件不存在，正在从 .env.example 复制...
    copy .env.example .env
    echo [WARN] 请编辑 .env 文件修改默认密码后再部署！
    exit /b 1
)
echo [INFO] 正在构建并启动所有服务...
docker compose up -d --build
echo [INFO] 部署完成！
echo [INFO]   前端地址: http://localhost
echo [INFO]   后端 API: http://localhost:8080/api
echo [INFO]   管理后台: 通过 http://localhost/pages/index.html 登录管理员账号进入
goto eof

:stop
echo [INFO] 正在停止所有服务...
docker compose down
echo [INFO] 已停止
goto eof

:restart
echo [INFO] 正在重启所有服务...
docker compose restart
echo [INFO] 重启完成
goto eof

:logs
docker compose logs -f --tail=100
goto eof

:status
docker compose ps
goto eof

:clean
echo [WARN] 此操作将删除所有数据（包括数据库）！
set /p confirm="确认删除？(y/N): "
if /i "!confirm!"=="y" (
    docker compose down -v
    echo [INFO] 已清理所有服务和数据卷
) else (
    echo [INFO] 已取消
)
goto eof

:usage
echo 用法: deploy.bat {start^|stop^|restart^|logs^|status^|clean}
echo.
echo   start   - 构建并启动所有服务
echo   stop    - 停止所有服务
echo   restart - 重启所有服务
echo   logs    - 查看实时日志
echo   status  - 查看服务状态
echo   clean   - 停止并删除所有数据（危险操作）
goto eof

:eof
