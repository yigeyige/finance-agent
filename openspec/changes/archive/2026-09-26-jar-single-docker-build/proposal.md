## Why

当前前端和后端只能分别构建，`deploy/` 也没有可复现的制品入口，无法证明同一版本的 Vue 工作台、可执行 JAR 和单应用镜像能够一起交付。现在建立统一构建链，冻结前端静态资源进入 JAR 的边界，并用非 root、只读优先的 Docker 运行方式降低部署风险。

## What Changes

- Maven 构建在前端已构建后把 `frontend/dist` 复制到后端 JAR 的 `static/` 目录。
- 增加多阶段 Dockerfile：Node 构建前端、Maven 构建 JAR、JRE 运行单应用镜像。
- 镜像使用非 root 用户、显式工作目录、健康检查，并通过启动说明约束 `--read-only`、临时目录和禁止 Docker socket/Compose。
- 增加统一制品构建脚本、`.dockerignore` 和 JAR/Docker 启动说明，确保 JAR 与镜像使用同一版本号。
- 增加 Web/Actuator 最小依赖，使静态首页和 `/actuator/health` 可作为用户验收入口。

## Capabilities

### New Capabilities

- `single-application-build`: 定义前端资源纳入可执行 JAR、版本一致和构建入口。
- `hardened-container-runtime`: 定义多阶段单应用镜像、非 root、健康检查、只读优先及无 Docker socket/Compose 边界。

### Modified Capabilities

无。

## Impact

- 影响 `backend/pom.xml`、后端资源配置和应用 Web/Actuator 依赖。
- 新增 `deploy/Dockerfile`、`deploy/.dockerignore`、`scripts/build-artifacts.ps1` 与部署说明。
- 构建过程依赖已配置的 Node/npm、Maven 和 Docker（Docker 由用户手动执行）；不启动长期运行服务或容器。
