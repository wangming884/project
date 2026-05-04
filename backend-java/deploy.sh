#!/bin/bash

# 校园综合服务平台 - 部署脚本
# 用于快速部署到生产环境

echo "=========================================="
echo "  校园综合服务平台 - 自动部署脚本"
echo "=========================================="
echo ""

# 配置变量
APP_NAME="campus-platform"
JAR_NAME="campus-platform-1.0.0.jar"
APP_PORT=8080
PROFILE="prod"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查 Java 环境
check_java() {
    echo -e "${YELLOW}检查 Java 环境...${NC}"
    if ! command -v java &> /dev/null; then
        echo -e "${RED}错误: 未找到 Java 环境，请先安装 JDK 17+${NC}"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo -e "${RED}错误: Java 版本过低，需要 JDK 17+${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✓ Java 环境检查通过${NC}"
}

# 停止旧进程
stop_old_process() {
    echo -e "${YELLOW}停止旧进程...${NC}"
    PID=$(ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}')
    
    if [ -n "$PID" ]; then
        echo "找到进程 PID: $PID"
        kill -15 $PID
        sleep 3
        
        # 检查是否成功停止
        if ps -p $PID > /dev/null; then
            echo -e "${RED}进程未能正常停止，强制终止...${NC}"
            kill -9 $PID
        fi
        echo -e "${GREEN}✓ 旧进程已停止${NC}"
    else
        echo "未找到运行中的进程"
    fi
}

# 备份旧版本
backup_old_version() {
    if [ -f "target/$JAR_NAME" ]; then
        echo -e "${YELLOW}备份旧版本...${NC}"
        BACKUP_DIR="backup/$(date +%Y%m%d_%H%M%S)"
        mkdir -p $BACKUP_DIR
        cp target/$JAR_NAME $BACKUP_DIR/
        echo -e "${GREEN}✓ 备份完成: $BACKUP_DIR${NC}"
    fi
}

# 编译打包
build_project() {
    echo -e "${YELLOW}开始编译打包...${NC}"
    
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}错误: 未找到 Maven，请先安装 Maven${NC}"
        exit 1
    fi
    
    mvn clean package -DskipTests
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}编译失败，请检查错误信息${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✓ 编译打包完成${NC}"
}

# 启动应用
start_application() {
    echo -e "${YELLOW}启动应用...${NC}"
    
    # 创建日志目录
    mkdir -p logs
    
    # 启动应用
    nohup java -jar \
        -Xms512m \
        -Xmx1024m \
        -Dspring.profiles.active=$PROFILE \
        -Dserver.port=$APP_PORT \
        target/$JAR_NAME \
        > logs/startup.log 2>&1 &
    
    echo $! > app.pid
    echo -e "${GREEN}✓ 应用已启动，PID: $(cat app.pid)${NC}"
}

# 检查启动状态
check_status() {
    echo -e "${YELLOW}检查启动状态...${NC}"
    sleep 5
    
    for i in {1..30}; do
        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$APP_PORT/api/actuator/health 2>/dev/null || echo "000")
        
        if [ "$HTTP_CODE" == "200" ]; then
            echo -e "${GREEN}✓ 应用启动成功！${NC}"
            echo ""
            echo "访问地址: http://localhost:$APP_PORT/api"
            echo "日志文件: logs/campus-platform.log"
            echo "启动日志: logs/startup.log"
            return 0
        fi
        
        echo -n "."
        sleep 2
    done
    
    echo ""
    echo -e "${RED}应用启动超时，请查看日志: logs/startup.log${NC}"
    tail -n 50 logs/startup.log
    exit 1
}

# 主流程
main() {
    echo "开始部署流程..."
    echo ""
    
    check_java
    stop_old_process
    backup_old_version
    build_project
    start_application
    check_status
    
    echo ""
    echo "=========================================="
    echo -e "${GREEN}  部署完成！${NC}"
    echo "=========================================="
}

# 执行主流程
main
