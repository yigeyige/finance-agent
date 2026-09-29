## Context

部署目标是前后端分离：后端 JAR/镜像监听 8080 并连接外部 PostgreSQL，前端 nginx 独立监听 8080；两者不通过仓库内 Compose 绑定。Spring Boot Actuator 已提供 `/actuator/health`、`/actuator/health/readiness` 和 `/actuator/health/liveness`。

## Decisions

1. 静态脚本检查 artifact builder 的 JAR 命名、prod profile 的 `FINANCE_AGENT_DB_*` 环境变量、Dockerfile 的 workdir/port/healthcheck/non-root 和无 socket/Compose。
2. 部署指南给出 JAR 启停命令、外部数据库变量、健康检查、Docker `--env-file`/`--stop` 流程和证据记录模板；不包含真实连接串或密码。
3. 前端 nginx 只提供静态页面，后端 API 地址由部署环境配置，不在本任务引入反向代理或 Compose。
