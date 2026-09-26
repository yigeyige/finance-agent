## Context

仓库已有 Vue 2.7.16/Vite 前端和 Spring Boot 后端，但前端 `dist` 未进入后端资源，后端也没有 Web/Actuator 运行入口。DEV-BOOT-008 要求测试/生产使用同一版本的可执行 JAR 与单应用镜像，同时避免把源码、缓存和凭证带入镜像。

## Goals / Non-Goals

**Goals:**

- 用 Maven 资源阶段复制已构建的 `frontend/dist` 到 `${project.build.outputDirectory}/static`，不把生成物提交到源代码目录。
- 用多阶段 Docker 构建隔离 Node/Maven 工具链，只从最终 JRE 阶段复制版本化 JAR。
- 提供非 root 用户、`/opt/finance-agent` 工作目录、健康检查和只读优先运行命令。
- 让统一脚本从 `backend/pom.xml` 读取版本，先构建前端、再构建 JAR，并可选构建同版本镜像。

**Non-Goals:**

- 不引入 Docker Compose、Kubernetes 清单或 Docker socket 挂载。
- 不实现业务 API、数据库迁移、生产密钥注入或长期运行验证。
- 不把 `frontend/dist`、Maven/npm 缓存或 `.env` 纳入 Git/镜像上下文。

## Decisions

1. **Maven 只消费前端构建产物。**
   - 采用 `maven-resources-plugin` 的 `copy-resources` 绑定 `process-resources`，使 JAR 内容可检查且不依赖运行时挂载前端目录。
   - 构建脚本负责先执行 `npm ci` 和 `npm run build`；缺少 `dist` 时 Maven 资源阶段失败，避免生成无前端的“成功”JAR。

2. **Docker 使用三阶段构建。**
   - Node 24.16.0 阶段构建 Vue；Maven + JDK 17 阶段构建 JAR；Eclipse Temurin 17 JRE 阶段只复制 JAR。
   - 最终阶段不复制源码、依赖缓存、`.env` 或 Docker CLI/socket。

3. **健康检查使用 Actuator。**
   - 增加 Spring Web 与 Actuator，使静态首页由 Spring Boot 提供，`/actuator/health` 作为容器健康检查。
   - 仅暴露 health 端点；不在本任务扩展业务诊断能力。

4. **版本由 POM 单一读取。**
   - `scripts/build-artifacts.ps1` 从 `backend/pom.xml` 读取 `project.version`，镜像构建通过 `APP_VERSION` 复用该值，JAR 文件名和镜像标签保持一致。

## Risks / Trade-offs

- [Risk] 前端构建和 Maven 依赖首次下载较慢 → 构建脚本保留 npm/Maven 原生缓存能力，但 `.dockerignore` 和最终镜像不携带缓存。
- [Risk] 基础镜像标签随上游更新 → 固定 Node、Maven/JDK 和 JRE 主版本标签，并在后续依赖安全任务中维护。
- [Risk] 只读根文件系统下某些框架需要临时目录 → 启动说明使用 `--tmpfs /tmp`，应用不写入工作目录。

## Migration Plan

1. 添加依赖和 Maven 静态资源复制配置。
2. 执行构建脚本生成 JAR，并检查 JAR 中存在 `static/index.html`。
3. 使用 Dockerfile 构建同版本镜像，静态检查用户、健康检查和禁止项。
4. 用户自行启动 JAR/镜像完成页面和健康端点人工验证；失败时删除本地产物并修复构建配置，不触碰数据库。

## Open Questions

无。基础镜像的具体补丁版本在后续安全扫描任务中继续冻结。
