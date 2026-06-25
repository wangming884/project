# 前端静态站点 — 使用 Nginx 托管
FROM nginx:1.25-alpine

# 删除默认配置
RUN rm /etc/nginx/conf.d/default.conf

# 复制自定义 Nginx 配置
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

# 复制前端页面和 JS
COPY pages/ /usr/share/nginx/html/pages/
COPY js/    /usr/share/nginx/html/js/

# index.html 放到根目录（首页入口）
COPY pages/index.html /usr/share/nginx/html/index.html

# 暴露端口
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
