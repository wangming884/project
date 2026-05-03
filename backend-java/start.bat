@echo off
chcp 65001 >nul
echo =========================================
echo 🚀 校园综合服务平台 - 启动中...
echo =========================================

REM 检查 Java 版本
echo 📋 检查 Java 环境...
java -version
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到 Java 环境，请先安装 JDK 17+
    pause
    exit /b 1
)

REM 检查 Maven
echo.
echo 📋 检查 Maven 环境...
mvn -version
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到 Maven，请先安装 Maven 3.6+
    pause
    exit /b 1
)

REM 清理并编译
echo.
echo 🔨 编译项目...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ❌ 编译失败，请检查错误信息
    pause
    exit /b 1
)

REM 启动应用
echo.
echo 🚀 启动应用...
java -jar target\campus-platform-1.0.0.jar

echo.
echo =========================================
echo ✅ 应用已停止
echo =========================================
pause
