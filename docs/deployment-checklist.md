# JAR 与独立 Docker 部署检查表

## 构建前静态检查

```powershell
pwsh -NoProfile -File scripts/qa-deployment-artifacts.ps1
```

该命令不构建、不启动服务、不连接数据库。需要构建制品时由用户执行：

```powershell
pwsh -NoProfile -File scripts/build-artifacts.ps1
pwsh -NoProfile -File scripts/build-artifacts.ps1 -BuildDocker
```

构建摘要应记录版本、JAR SHA-256、前端 dist SHA-256 和镜像摘要；不要记录数据库密码、连接串中的凭据或真实业务数据。

## JAR 模式（后端）

后端使用外部 PostgreSQL，不依赖前端容器或 Docker socket。用户在当前进程设置 `FINANCE_AGENT_DB_URL`、`FINANCE_AGENT_DB_USERNAME`、`FINANCE_AGENT_DB_PASSWORD` 后，用对应版本的 `backend/target/finance-agent-<version>.jar` 启动，并通过以下地址验证：

- `/actuator/health`
- `/actuator/health/readiness`
- `/actuator/health/liveness`

停止时使用当前进程的正常终止信号，确认进程退出后再回收工作目录；不要使用清库或强制覆盖方式处理失败实例。

## Docker 模式（前后端分离）

后端和前端分别构建、分别启动，均监听容器内 8080；不提供 Compose，不挂载 Docker socket。后端通过 `--env-file` 或等价的受控环境注入 PostgreSQL 变量，前端只提供 nginx 静态页面。用户分别记录容器 ID、镜像摘要、健康检查结果和停止结果，完成后执行 `docker stop` 并确认容器退出。

人工结果至少包含：制品版本/摘要、工作目录、端口映射、health/readiness 状态、数据库连接结果、停止时间、限制和结论。
