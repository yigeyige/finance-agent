## Why

本地启动需要分别导入 `.env` 并启动前后端，容易遗漏后端数据库变量，导致身份接口失败并连带隐藏前端菜单。需要提供一个统一、可重复执行的 local 启动入口。

## What Changes

- 在 `scripts/` 中新增 PowerShell 本地启动脚本。
- 脚本从仓库根目录 `.env` 读取进程环境变量后启动 Spring Boot 后端和 Vite 前端。
- 脚本在端口已监听时复用已有服务，输出访问地址和日志位置，且不显示敏感配置值。

## Capabilities

### New Capabilities

- `local-development-startup`: 为 Windows PowerShell 提供统一的前后端 local 启动入口和可重复执行行为。

### Modified Capabilities

- 无。

## Impact

- 影响 `scripts/` 下的开发辅助脚本与本地开发启动方式。
- 不修改业务接口、权限逻辑、数据库结构或生产部署行为。
