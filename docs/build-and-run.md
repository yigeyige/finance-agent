# 前后端分离构建与部署

DEV-BOOT-008 使用同一个 `backend/pom.xml` 的 `project.version` 生成独立的 Vue 2.7.16 静态产物、后端可执行 JAR、后端 Java 镜像和前端 Nginx 镜像。前端不再复制到 JAR，前后端由部署网关或环境配置提供 API 路由。

## 构建制品

在仓库根目录执行：

```powershell
./scripts/build-artifacts.ps1
```

脚本会执行 `npm ci`、`npm run build` 和 `mvn -f backend\pom.xml -DskipTests package`，并输出：

- `frontend/dist/` 及其目录 SHA-256；
- `backend/target/finance-agent-<version>.jar` 及其 SHA-256；
- 从 `backend/pom.xml` 读取的统一版本号。

使用仓库 Maven Wrapper 时：

```powershell
./scripts/build-artifacts.ps1 -MavenCommand .\mvnw.cmd
```

后端 JAR 是 API/Actuator 运行制品，不应包含 `BOOT-INF/classes/static/index.html`；前端 `dist/index.html` 是独立交付物。

## 构建两个镜像

在已安装并登录 Docker 的环境中执行：

```powershell
./scripts/build-artifacts.ps1 -BuildDocker
```

默认生成同一版本的两个镜像：

```text
finance-agent-backend:<version>
finance-agent-frontend:<version>
```

也可以手工指定同一版本：

```powershell
docker build -f deploy/backend.Dockerfile --build-arg APP_VERSION=0.1.0 -t finance-agent-backend:0.1.0 .
docker build -f deploy/frontend.Dockerfile --build-arg APP_VERSION=0.1.0 -t finance-agent-frontend:0.1.0 .
```

后端镜像只复制 JAR；前端镜像由 Node 构建后复制到 Nginx。两个最终镜像均不包含源码、依赖缓存、`.env` 或 Docker CLI/socket。前端镜像监听 `8080` 并支持 SPA fallback，后端镜像监听 `8080` 并提供 `/actuator/health`。

## 用户启动与人工验收

AI 不启动长期运行进程。用户可分别启动 JAR 和两个容器；运行时环境文件应放在仓库外：

### 后端 JAR

```powershell
java -jar backend\target\finance-agent-0.1.0.jar --spring.profiles.active=local
```

### 后端容器

```powershell
docker run --rm --read-only `
  --tmpfs /tmp:rw,noexec,nosuid,size=64m `
  --env-file C:\path\to\finance-agent-backend.env `
  -p 8081:8080 finance-agent-backend:0.1.0
```

### 前端容器

```powershell
docker run --rm --read-only `
  --tmpfs /tmp:rw,noexec,nosuid,size=16m `
  -p 8080:8080 finance-agent-frontend:0.1.0
```

实际环境由网关/API 路由把前端工作台请求转发到后端服务；通用镜像不写死后端主机名或密钥，也不依赖 Compose。

验收入口：

- 前端页面：`http://localhost:8080/`
- 后端健康端点：`http://localhost:8081/actuator/health`
- 后端就绪端点：`http://localhost:8081/actuator/health/readiness`
- 后端诊断 API：`http://localhost:8081/api/v1/diagnostics`
- 后端 API：按部署网关配置的 API 路由检查，不以静态页面返回代替 API 验证。

诊断人工验收可使用以下只读请求（不需要提交真实凭证）：

```powershell
Invoke-WebRequest http://localhost:8081/actuator/health/readiness | Select-Object -Expand Content
Invoke-WebRequest http://localhost:8081/api/v1/diagnostics -Headers @{
  'X-Trace-Id'='manual-check-001'; 'X-Task-Id'='task-check-001';
  'X-Step-Id'='step-check-001'; 'X-Invocation-Id'='invocation-check-001'
} | Select-Object -Expand Content
```

后端控制台日志使用 Spring Boot 结构化 JSON 格式，包含有限的 `traceId`、`taskId`、`stepId`、`invocationId` 上下文。日志字段过滤器会阻断密钥、密码、token、禁止字段以及完整 prompt/response/reasoning；人工验收时应确认这些原文不出现在日志中。

本任务不提供 Compose 文件，不要求挂载 Docker socket，也不由应用创建数据库或管理员对象。
