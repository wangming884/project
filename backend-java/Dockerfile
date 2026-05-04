# 使用 OpenJDK 17 作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制 jar 文件
COPY target/campus-platform-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 启动应用
ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", "-Dspring.profiles.active=prod", "app.jar"]
