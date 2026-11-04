---
id: 20261104-docker-compose-deployment
title: "Docker Compose 部署脚本"
status: done
priority: P1
owner: yigeyige99
created_at: 2026-11-04
type: feat
source: conversation
openspec_change: ""
---

## 需求

### ID&标题

Docker Compose 部署脚本

### 描述

为 finance-agent 提供 docker-compose 部署脚本：PostgreSQL（含 pgvector）→ 迁移 → 后端 → 前端；本地构建镜像后可 `docker save/scp/load` 到远程。当前先完成脚本和本地构建/冒烟，远程镜像打包暂缓（远程 OS 未确认）。

## 背景

- 后端不自动执行 Flyway，项目使用独立 `FlywayMigrationRunner`；
- 仓库缺少 V40–V43 迁移文件（可从 `61566d2` 恢复），其中 V40/V42 是 schema 变更，不能建空文件；
- V7 使用 `vector(3)`，但没有迁移创建 `vector` 扩展；
- `deploy/nginx.conf` 缺少 `/api` 反向代理；
- 统一网关规划：网关 → 前端 `8192`、后端 `8193`，网关需把 `proxy_read_timeout` 调到 660s。

## 目标

- [x] 从 `61566d2` 恢复 V40–V43 迁移文件。
- [x] 新增 `deploy/docker-compose.yml`：db、migrate、backend、frontend，端口由 compose 控制（当前前端 9090、后端 9091），持久化 `pgdata`、`backend-runtime`。
- [x] 新增 `deploy/initdb/01-extensions.sql`：创建 `vector` 扩展。
- [x] 更新 `deploy/nginx.conf` 与模板：监听 `${FRONTEND_PORT}`，增加 `/api` 代理（`${BACKEND_PORT}`）与 660s 超时。
- [x] 更新后端/前端 Dockerfile 的端口与健康检查（端口不写死，由 compose 注入）。
- [x] 本地构建镜像并用全新 DB 跑 compose 冒烟（已执行：host 网络，9090/9091，IP 可访问，登录 200）。
- [x] 远程镜像打包/传输：脚本已就绪；远程加载/部署由用户按 CentOS 环境执行。

## 验收标准

- 全新 DB 能依次应用 V1–V53，`investigation_prompt` 为 `text`、`table_hints` 存在。
- `db` 健康后 `migrate` 成功，`backend` 启动并通过 `/actuator/health`，前端 9090 可登录。
- `pgdata` 与 `backend-runtime` volume 持久化，`docker compose down` 不清数据。
- 网关 `/api` 到后端 9091 的 `proxy_read_timeout` 为 660s。

## 执行记录

- 2026-11-04：恢复 V40–V43；新增 compose/initdb；更新 nginx 与 Dockerfile 端口/健康检查。
- 2026-11-04：`docker-compose v2.24 -f deploy/docker-compose.yml --env-file .env config` 校验通过（EXIT=0）。
- 2026-11-04：本地未安装 `docker compose` 插件，但有独立 `docker-compose v2.24`；镜像构建与全新库 compose 冒烟待执行，远程镜像传输待远程 OS/网关确认。
- 2026-11-04：本地 compose 冒烟通过：db healthy、migrate 53 个版本（当前 V53）、backend 9091 healthy、frontend 9090 healthy；`127.0.0.1` 与 `10.54.20.23` 的 9090/9091 均 200；`10.54.20.23:9090/api/v1/identity/login` 登录 200。
- 2026-11-04：为避开本机 8192（nginx-gui）与 Docker 端口映射/防火墙问题，compose 改为 `network_mode: host`：前端 9090、后端 9091、DB 5432；前端容器 Nginx 的 `/api` 代理改为 `127.0.0.1:9091`。持久化卷 `deploy_pgdata`、`deploy_backend-runtime`；本机原 `finance-agent-postgres` 容器与 `finance-agent-pgdata` 未改动。
- 2026-11-04：端口改为 compose 注入：后端 `FINANCE_AGENT_PORT`，前端 `FRONTEND_PORT`/`BACKEND_PORT`；Nginx 使用模板 + 官方 entrypoint envsubst 渲染。前端构建阶段切换为 `registry.steam.crcloud.com/tools/node:24-alpine-amd64`（Node 24.5.0/Alpine），`npm ci` + Vite 构建通过；运行时仍为 `nginx:1.27-alpine`。
- 2026-11-04：最终复验：渲染后的 `conf.d/default.conf` 为 `listen 9090`、`proxy_pass http://127.0.0.1:9091`；`127.0.0.1:9090` 与 `10.54.20.23:9090` 均 200；`10.54.20.23:9090/api/v1/identity/login` 登录 200。
