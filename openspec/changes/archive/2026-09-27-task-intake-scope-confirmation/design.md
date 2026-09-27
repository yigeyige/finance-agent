## Context

任务状态机、任务/前提/范围领域对象和 MyBatis-Plus 仓储已经存在，但还没有把这些能力编排成任务发起 API。`ScopeRevision` 的数据库模型要求确认人、摘要和版本化 JSON，适合把范围确认作为一次不可变追加；任务创建仍需保留 `DRAFT` 语义，不能在收到自然语言问题时启动调查。

## Goals / Non-Goals

**Goals:**

- 通过事务应用服务创建任务草稿、追加运维前提并确认范围。
- 使用 `TaskStateTransitionService` 作为唯一状态推进入口，并在确认范围时绑定新的 `currentScopeRevisionId`。
- 将读取、留存、送模字段三类权限作为独立 JSON 文档保存，禁止从自然语言字段推断或扩大范围。
- 提供带 Bean Validation 的 REST DTO、OpenAPI 3.1 路径和 JSON Schema 契约。
- 复用审计摘要链记录任务创建、前提追加和范围确认，审计 payload 只保留摘要元数据，不保存原始模型内容。

**Non-Goals:**

- 不接入真实财务源系统、模型或外部身份服务。
- 不实现步骤提议、步骤审批、Worker、SSE 或决策包生成。
- 不引入新的运行时依赖、数据库表或原位修改范围历史。

## Decisions

### 1. 应用服务编排三类写操作

新增 `TaskIntakeApplicationService`，以事务方法提供创建任务、追加前提和确认范围。创建任务先落库为 `DRAFT`；确认范围时追加不可变 `ScopeRevision`，再通过状态服务推进到 `READY`。若任务仍为 `DRAFT`，先转入 `WAITING_SCOPE_CONFIRMATION` 再确认，保证每个状态变化都有状态事件。

替代方案是让 Controller 直接调用多个仓储，但这会绕过状态守卫和审计事务，因此不采用。

### 2. 范围确认请求直接成为 revision

本任务不增加“草拟范围”表；REST 请求中的结构化范围在确认接口通过严格 `VersionedJson` 校验后一次性生成 revision。未确认的自然语言补充仅保留在请求之外，不参与任何运行时查询条件。

### 3. 复用审计摘要链

抽取 `TaskAuditEventAppender`，统一读取任务最新审计事件、生成规范化最小 payload 并追加 `AuditEvent`。状态事件仓储和任务发起服务共同复用该组件，避免复制摘要链逻辑。

### 4. 契约源优先

在 `contracts/openapi/finance-agent-v1.json` 增加任务发起、前提和范围确认路径，并新增独立 Draft 2020-12 schema；Java 请求对象只负责运行时 Bean Validation，不成为契约事实源。契约校验脚本继续作为无网络验证入口。

## Risks / Trade-offs

- [范围变更后的旧审批/草稿尚未有本任务对应的完整应用仓储] → 本任务只允许在 `DRAFT`、`WAITING_SCOPE_CONFIRMATION` 或 `READY` 阶段确认范围，并将 `READY` 的新范围确认重新置于范围确认流程；步骤审批与草稿 stale 的具体记录由后续 DEV-RUN-003/后续任务实现。
- [请求包含大段原始问题] → 任务表只保存用户原始问题，审计 payload 仅保存摘要和标识，不记录完整请求。
- [当前认证上下文尚未接入] → API 暂以必填 `X-Actor-User-Id` 作为显式操作者边界，后续统一认证适配时替换来源，不改变服务接口语义。

## Migration Plan

1. 先部署应用代码和版本化契约；无需新增表，复用现有 `task.investigation_task`、`task.task_premise` 和 `task.scope_revision`。
2. 运行后端编译、单元/契约测试和 `scripts/verify-contracts.py`。
3. 回滚时移除新 Controller/Service 版本；已追加的任务、前提、范围和审计记录保持只读，不覆盖历史。

## Open Questions

- 统一认证接入后，`X-Actor-User-Id` 是否由网关映射为安全上下文而不再允许客户端直接提供。
- DEV-RUN-003 落地时，旧范围如何批量将步骤审批和决策包草稿标记为 `STALE`，需要结合其仓储接口确定事务顺序。
