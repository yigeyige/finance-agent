## Why

Harness workspace 需要按用户、任务和 session 隔离，并且所有路径、大小和媒体类型都必须在服务端受控。当前只有 workspace 根目录和引用模型，缺少统一的路径策略、配额和清理入口，绝对路径、遍历或符号链接可能越界。

## What Changes

- 增加按 user/task/session 生成的 workspace manager 和服务端路径策略。
- 增加允许媒体类型、单文件/目录配额及安全写入、读取、清理接口。
- 拒绝绝对路径、`..` 遍历、符号链接逃逸和不匹配业务边界的访问。
- 增加 local/test workspace 配置；workspace 只保存临时副本和 digest，权威证据继续由证据/对象存储边界维护。
- 增加路径、隔离、配额、类型和清理契约测试。

## Capabilities

### New Capabilities

- `controlled-workspace`: 受控 workspace 的隔离、路径和配额契约。

### Modified Capabilities

无。

## Impact

- 影响 `harness/application`、`harness/infrastructure` 和 `FinanceAgentProperties.Workspace`。
- 复用 JDK NIO，不增加依赖；默认 local/test 继续使用本地临时目录，prod 需显式配置根路径和配额。
