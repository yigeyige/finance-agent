## Context

workspace 是 Harness 的临时工作区，不是权威证据库。当前项目已有 `WorkspaceRef` 只保存相对路径、对象引用和 digest，也有对象存储路径防护，但缺少 task/session 目录生命周期和 symlink 解析策略。

## Goals / Non-Goals

**Goals:**

- 为每个 user/task/session 生成不可由请求覆盖的目录。
- 通过 NIO normalize + real path 校验防止绝对路径、遍历和符号链接逃逸。
- 在写入前后执行媒体类型、单文件大小和 workspace 总配额检查，并提供清理接口。
- 返回带 digest 的临时制品结果，调用方可另行持久化 `WorkspaceRef`，不把 workspace 作为唯一证据副本。

**Non-Goals:**

- 不实现对象存储、证据表写入或用户授权认证；业务边界由上层身份和 repository 负责。
- 不允许任意 shell/filesystem tool 绕过 manager；不改变 Docker sandbox 任务。

## Decisions

- 目录布局固定为 `<root>/<userId>/<taskId>/<sessionId>/`，ID 由服务端生成/传入，路径 API 不接受 root 覆盖。
- 目标路径必须位于 workspace real path 下；已有路径和父目录都执行 `toRealPath()`，符号链接一律拒绝，避免 normalize 后的假安全。
- 写入采用临时文件 + 原子移动，边读边计算 SHA-256，并在达到单文件或总配额时中止和清理临时文件。
- 允许媒体类型使用配置白名单，默认只允许 `application/json`、`text/plain`、`text/csv`；清理按完整 user/task/session 边界执行。
- 不复用对象存储 gateway 作为 workspace manager，因为 workspace 需要三层业务隔离和生命周期清理；底层仍复用 JDK NIO 标准能力。

## Risks / Trade-offs

- [Risk] 并发写入造成配额竞态 → [Mitigation] 每个 workspace 使用进程内锁，写入前重新统计并在临时文件移动前复核。
- [Risk] 外部进程在校验后替换符号链接 → [Mitigation] 写入/读取前后都检查 real path；生产高风险场景仍需后续 sandbox 任务。
- [Risk] 清理误删共享根目录 → [Mitigation] 只允许删除三层完整 ID 路径，并验证目标严格位于 root 下。

## Migration Plan

增加配置字段但提供 local/test 默认值；无需数据库迁移。先运行离线路径契约测试和 Maven 验证，再由用户人工检查任务目录隔离。回滚只需恢复 manager 与配置字段，已有 workspace 临时目录可手工清理。
