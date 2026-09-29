## Why

JAR 和前后端 Dockerfile 已存在，但缺少一份部署前静态检查，无法统一确认外部 PostgreSQL 环境变量、端口、工作目录、health/readiness、停止方式、非 root 和 Docker socket 隔离，也容易误把前后端打包成 Compose 依赖。

## What Changes

- 新增 JAR/前后端镜像部署检查脚本和部署检查表，固定外部数据库变量、端口、健康检查和停止流程。
- 新增后端契约测试，校验现有 `build-artifacts.ps1`、Dockerfile、生产 profile 和 nginx 独立交付边界。
- 默认只做静态检查；制品构建和容器启动由用户按检查表手动执行。

## Capabilities

### New Capabilities

- `jar-docker-deployment-gate`: 提供 JAR、前端和后端独立镜像的部署前检查与手动验收记录。

### Modified Capabilities

- 无

## Impact

- 新增部署检查脚本、指南和后端契约测试。
- 不新增 Compose、不自动构建镜像、不启动容器、不连接外部数据库。
