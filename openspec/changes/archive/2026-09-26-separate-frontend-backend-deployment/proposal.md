## Why

DEV-BOOT-008 目前把 Vue 工作台打入 Spring Boot JAR，并以单应用镜像同时承载前端和后端。该交付形态与目标部署边界不一致：前端静态资源应独立发布、后端应作为 API 服务独立扩缩和纳管，也不应因为前端资源变化而重建后端运行制品。

## What Changes

- **BREAKING**：后端 JAR 不再包含 `frontend/dist`，只交付 Spring Boot API/Actuator 运行制品。
- 将构建产物拆分为同版本的后端 JAR、后端镜像和前端静态镜像。
- 使用独立的前端 Nginx 非 root 镜像承载 Vue 2.7.16 SPA；前端镜像不包含源码、依赖缓存、密钥或后端 JAR。
- 更新构建脚本、Docker 构建入口、启动说明、任务卡和方案基线，明确前后端分离部署与 API 入口由部署网关/环境配置提供。
- 保留不使用 Compose、不挂载 Docker socket、只读优先文件系统和健康检查约束。

## Capabilities

### New Capabilities

- `separated-frontend-backend-build`: 独立生成前端静态产物、后端 JAR 及一致版本标识。
- `separated-container-runtime`: 以前端 Nginx 镜像和后端 Java 镜像分别运行，均使用非 root、健康检查和最小运行内容。

### Modified Capabilities

<!-- 本次变更替换未归档的单应用构建约定；归档时由新能力规格承接新的部署契约。 -->

## Impact

- `backend/pom.xml`、`backend/src/main/resources/application.properties`、`scripts/build-artifacts.ps1`。
- `deploy/` 下 Dockerfile、Nginx 配置及根 `.dockerignore`。
- `docs/build-and-run.md`、`solutions/` 中的技术方案、开发任务基线及 DEV-BOOT-008 任务记录。
- 需要用户分别启动前端和后端进行页面、API/健康端点联调；本次不启动长期运行服务。
