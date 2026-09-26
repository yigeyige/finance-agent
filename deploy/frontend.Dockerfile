FROM node:24.16.0-bookworm-slim AS frontend-build

WORKDIR /workspace/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM nginx:1.27-alpine

ARG APP_VERSION=0.1.0
RUN rm -rf /usr/share/nginx/html/* /etc/nginx/conf.d/* \
    && mkdir -p /tmp/client_temp /tmp/proxy_temp /tmp/fastcgi_temp /tmp/uwsgi_temp /tmp/scgi_temp \
    && chown -R nginx:nginx /tmp/client_temp /tmp/proxy_temp /tmp/fastcgi_temp /tmp/uwsgi_temp /tmp/scgi_temp

COPY --from=frontend-build --chown=nginx:nginx /workspace/frontend/dist/ /usr/share/nginx/html/
COPY --chown=nginx:nginx deploy/nginx.conf /etc/nginx/nginx.conf

LABEL org.opencontainers.image.title="finance-agent-frontend" \
      org.opencontainers.image.version="${APP_VERSION}"

USER nginx
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --quiet --output-document=- http://127.0.0.1:8080/ > /dev/null || exit 1

ENTRYPOINT ["nginx", "-g", "daemon off;"]
