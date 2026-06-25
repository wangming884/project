# ========== 阶段 1: 构建 ==========
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
# 先下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B
COPY src/ ./src/
RUN mvn clean package -DskipTests -B

# ========== 阶段 2: 运行 ==========
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata curl \
    && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone

# 从构建阶段复制 jar
COPY --from=builder /build/target/campus-platform-*.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM 调优 + 生产配置
ENTRYPOINT ["java", \
  "-Xms256m", "-Xmx512m", \
  "-XX:+UseG1GC", \
  "-XX:MaxGCPauseMillis=200", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.jar"]
