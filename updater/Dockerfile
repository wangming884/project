FROM python:3.11-alpine

# 安装 git 和 docker CLI
RUN apk add --no-cache git docker docker-cli-compose

WORKDIR /app
COPY app.py .

EXPOSE 8081

CMD ["python", "app.py"]
