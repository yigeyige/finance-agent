## Why

DEV-RUN-001 已提供任务状态转移入口，但当前仓储只有基础领域对象和持久化端口，缺少从用户请求创建任务草稿、保存运维提供的前提、生成并确认不可变范围修订的应用闭环。若没有这个边界，范围可能被自然语言补充隐式扩大，未确认范围也可能被后续运行时误用。

## What Changes

- 增加任务发起应用服务，将原始问题和结构化对象线索保存为 `DRAFT` 任务，不在创建请求中启动步骤执行。
- 增加前提录入接口，明确记录提供人、来源说明和独立核实状态；输入前提不改写为源系统事实。
- 增加范围草拟与确认流程，分别保存对象、生效时间、包含/排除路径以及读取/留存/送模三类字段权限。
- 范围确认只追加不可变 `ScopeRevision`，确认后通过权威状态服务进入 `READY`；新范围修订使旧审批和草稿失效。
- 增加 REST 请求/响应模型、契约校验和最小审计事件，禁止未确认范围进入步骤运行路径。

## Capabilities

### New Capabilities

- `task-intake-scope-confirmation`: 任务发起、前提记录、范围草拟/确认和 REST 契约。

### Modified Capabilities

- `task-scope-step-model`: 补充范围确认前后的不可变修订、三类字段权限隔离和旧审批/草稿 stale 约束。

## Impact

- 影响 `task` 模块的应用服务、REST Controller、领域请求模型、范围/前提仓储和审计适配器。
- 复用现有 `TaskStateTransitionService`、MyBatis-Plus 仓储、`AuditEventRepository`、`VersionedJson` 和 Spring MVC 校验能力。
- 不新增外部依赖、不访问真实源系统、不启动运行时；默认测试保持网络零调用。
