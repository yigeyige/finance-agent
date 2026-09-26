## Context

当前构建流程先编译 Vue，再由 Maven 把 `frontend/dist` 复制到 JAR 的 `static/` 目录，并用单个 Spring Boot 镜像同时提供页面和 API。目标部署要求前后端分离，因此必须撤销资源复制约定，同时保持后端可执行 JAR 和前端静态文件都可独立验收。

## Decisions

### 1. Artifact boundaries

- `backend/pom.xml` 保留 Web 和 Actuator 依赖，移除 Maven 对 `frontend/dist` 的资源复制执行。
- `scripts/build-artifacts.ps1` 按同一 `project.version` 依次构建前端和后端，分别输出 `frontend/dist` 摘要和后端 JAR 摘要。
- 后端 JAR 必须不包含 `BOOT-INF/classes/static/index.html`；前端 `dist/index.html` 是独立交付物。

### 2. Image boundaries

- `deploy/backend.Dockerfile` 只复制后端 JAR，最终镜像以 UID/GID `10001` 运行，并通过 `/actuator/health` 健康检查。
- `deploy/frontend.Dockerfile` 使用 Node 构建 Vue 静态文件，再复制到 Nginx 非 root 运行时；Nginx 监听 8080，支持 SPA fallback，健康检查根页面。
- 前端镜像不内置后端地址代理规则；生产入口由网关/反向代理或前端 API 配置决定，避免把环境地址写死在镜像中。
- 两个镜像使用同一个版本值，但使用不同仓库名：`finance-agent-backend:<version>`、`finance-agent-frontend:<version>`。

### 3. Runtime and security

- 两个运行时都不需要 Compose 或 Docker socket；启动说明使用 `--read-only` 和受限 `/tmp`。
- 根 `.dockerignore` 继续排除 `.env`、密钥、源码缓存和本地产物。最终镜像只保留对应运行时内容。
- 本次只做构建、镜像元数据和静态负向检查，不启动长期运行的 JAR/容器；页面、API 和健康端点由用户手动验证。

## Data flow

```text
frontend source -> frontend.Dockerfile -> frontend image (Nginx:8080)
backend source  -> Maven package      -> backend JAR -> backend image (Java:8080)
                                                   \-> /actuator/health
browser -> deployment gateway -> frontend static + backend API routing
```

## Alternatives considered

- **继续单 JAR/单镜像**：拒绝；前端变化会耦合后端制品，且不满足分离部署边界。
- **在前端镜像内固定反向代理到后端服务名**：暂不采用；服务发现和网关命名属于环境事实，不能写死在通用基线镜像。
- **引入 Compose/Kubernetes 清单**：暂不采用；当前任务只交付两个独立制品和启动契约，不扩大部署编排范围。
