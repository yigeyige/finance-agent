FROM node:24.16.0-bookworm-slim AS frontend-build

WORKDIR /workspace/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM registry.steam.crcloud.com/tools/nginx:1.28.0

ARG APP_VERSION=0.1.0
ENV FRONTEND_PORT=8080
ENV BACKEND_PORT=8080
RUN rm -rf /usr/share/nginx/html/* /etc/nginx/conf.d/* \
    && mkdir -p /tmp/client_temp /tmp/proxy_temp /tmp/fastcgi_temp /tmp/uwsgi_temp /tmp/scgi_temp \
    && chown -R nginx:nginx /tmp/client_temp /tmp/proxy_temp /tmp/fastcgi_temp /tmp/uwsgi_temp /tmp/scgi_temp

COPY --from=frontend-build --chown=nginx:nginx /workspace/frontend/dist/ /usr/share/nginx/html/
COPY --chown=root:root deploy/nginx.conf /etc/nginx/nginx.conf
COPY --chown=root:root deploy/nginx/templates/ /etc/nginx/templates/

LABEL org.opencontainers.image.title="finance-agent-frontend" \
      org.opencontainers.image.version="${APP_VERSION}"

# 由官方 entrypoint 的 envsubst 把模板渲染到 conf.d；master 需要 root 写配置，worker 仍为 nginx。
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl --fail --silent http://127.0.0.1:${FRONTEND_PORT:-8080}/ > /dev/null || exit 1

# 不覆盖官方 ENTRYPOINT，让 /docker-entrypoint.d/20-envsubst-on-templates.sh 渲染模板。
