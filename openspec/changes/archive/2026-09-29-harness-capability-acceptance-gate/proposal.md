## Why

Harness 的 Context Compression、受控 Workspace、受控 Skills、受控 Subagents、Long-term Memory 和 Docker Sandbox 已分别实现，但缺少统一的能力验收表，无法在一次检查中记录版本、输入、结果、限制和需人工执行的场景。

## What Changes

- 新增版本化 Harness 能力验收 fixture，逐项声明功能、安全、故障和人工验证要求。
- 新增默认离线的 PowerShell 验收脚本，校验能力配置、白名单和结果记录完整性。
- 新增后端契约测试，覆盖六项能力的现有测试入口、生产白名单和 Docker socket 隔离。

## Capabilities

### New Capabilities

- `harness-capability-acceptance-gate`: 提供 Harness 六项能力的统一验收表与证据约束。

### Modified Capabilities

- 无

## Impact

- 新增 `scripts/fixtures/`、验收脚本和后端契约测试。
- 不新增运行时依赖，不启动 Docker 或本地服务；需要环境的人工验证由用户执行。
