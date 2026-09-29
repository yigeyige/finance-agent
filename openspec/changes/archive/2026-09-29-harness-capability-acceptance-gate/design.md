## Context

Harness 能力涉及不同安全边界：压缩不能提升事实级别，Workspace 不能越界，Skills/Subagents 必须白名单，Memory 必须审批且仅规划用途，Sandbox 必须无 Docker socket。统一 fixture 只描述验收元数据，不复制实现逻辑。

## Decisions

1. 六项能力分别记录 `capability`、`version`、`functionalChecks`、`securityChecks`、`failureChecks`、`manualRequired` 和 `evidence`。
2. PowerShell 默认只校验 fixture 和部署文件中的安全声明；`-Execute` 读取用户提供的结果 JSON，不启动 Docker、应用或网络调用。
3. 后端契约测试复用现有能力测试类，补充 fixture 完整性和 HarnessAgentFactory/部署 Docker socket 隔离检查。
4. 生产权限只允许已声明白名单，Sandbox 仅允许 `JAR_LOCAL` 受控模式；`APPLICATION_CONTAINER` 不提供 Docker socket。
