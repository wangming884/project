#!/bin/bash

# 校园综合服务平台 - 快速启动脚本

echo "========================================="
echo "🚀 校园综合服务平台 - 启动中..."
echo "========================================="

# 检查 Java 版本
echo "📋 检查 Java 环境..."
java -version

if [ $? -ne 0 ]; then
    echo "❌ 错误: 未找到 Java 环境，请先安装 JDK 17+"
    exit 1
fi

# 检查 Maven
echo ""
echo "📋 检查 Maven 环境..."
mvn -version

if [ $? -ne 0 ]; then
    echo "❌ 错误: 未找到 Maven，请先安装 Maven 3.6+"
    exit 1
fi

# 清理并编译
echo ""
echo "🔨 编译项目..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 编译失败，请检查错误信息"
    exit 1
fi

# 启动应用
echo ""
echo "🚀 启动应用..."
java -jar target/campus-platform-1.0.0.jar

echo ""
echo "========================================="
echo "✅ 应用已停止"
echo "========================================="
