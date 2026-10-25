# Design

## Context

- 现状：`BusinessModelInteractionService.guideBusinessHandling` 每次调用重新 `collect` 证据（`EmsDatabaseAssistanceService.collect` 从空列表开始），证据只随 `guidance_draft` 的最新一份草稿展示，跨轮不累积。
- `task.guidance_draft`（V18）append-only 保存草稿全文 JSON，读最新。
- 已有 `packet` 包（调查决策包）绑定 `scope_revision_id`/`runtime_bundle_id` + 复核/发布/交接状态机，与本场景（管理员自用、生成即完成）无关，不接入。
- 证据结构 `EmsDatabaseEvidence(schema, table, purpose, columns, rows)`；业务处理草稿 `BusinessHandlingDraft`（单轮 evidence 上限 10）。
- Flyway 迁移最新到 V19，新迁移用 V20。

## Goals / Non-Goals

**Goals:**
- 证据按轮追加累积，不覆盖历史轮次。
- 一键生成业务决策包：证据去重合并、revision 递增、旧版保留。
- 前端证据面板切累积视图，新增生成与查看入口。

**Non-Goals:**
- 不接入 `packet` 的复核/发布/交接、`scope`/`runtimeBundle`。
- 不做多管理员并发的强一致 round 分配（管理员单用户操作，用 `max(round_no)+1` + 唯一约束兜底）。
- 不修改 `guidance_draft` 表语义与 `packet` 表。

## Decisions

1. **新独立模块 `businesspacket`**（domain/application/infrastructure/web 四层），不复用 `packet` 包，避免 scope/runtimeBundle 与状态机耦合。

2. **两张新表，挂在 `task` schema**（与 `guidance_draft` 同级）：

   - `task.business_evidence_accum`：`evidence_accum_id`、`task_id`、`round_no`、`evidence(jsonb)`、`created_at`，`UNIQUE(task_id, round_no)`。append-only，`round_no` = 当前任务 `max(round_no)+1`。
   - `task.business_decision_packet`：`packet_id`、`task_id`、`revision`、`canonical_content(jsonb)`、`content_digest(char64)`、`created_at`，`UNIQUE(task_id, revision)`。revision 递增、旧版保留。

3. **证据累积写入点**：在 `guideBusinessHandling` 中 `collect` 完成后、保存草稿时，把当轮 `evidence` 追加写入 `business_evidence_accum`（仅在有证据时写入，空轮不写）。

4. **决策包生成**（新 `BusinessDecisionPacketService`）：
   - 读最新草稿；无草稿 → 拒绝并提示。
   - 读工作区全部证据（按 round 升序），用 `LinkedHashMap` 以 `(schema, table, purpose)` 为键去重、后轮覆盖前轮（保留最新），去重后截断到 50 条。
   - 装配 canonical JSON 三块：标识（packet_id/task_id/revision/created_at）+ 业务处理主体与结论建议（`BusinessHandlingDraft` 全字段，不含其单轮 evidence）+ evidence（去重结果）。
   - 计算 `content_digest`（sha256），与上一 revision 比较，相同 → 跳过不新增；不同 → `revision+1` 落库。

5. **接口**（`BusinessDecisionPacketController`）：`POST /api/v1/tasks/{taskId}/decision-packet`（生成）、`GET .../decision-packet/latest` 与 `/revisions/{revision}`（查看）、`GET .../decision-packet/evidence`（累积证据，供前端面板）。全部只读/写自有库，不触碰财务系统。

6. **前端**：`WorkbenchHome.vue` 证据面板改为请求累积证据接口；新增"生成业务决策包"按钮与决策包查看区。

## Risks / Trade-offs

- 并发 `round_no` 竞态 → 管理员单用户场景风险低；`UNIQUE(task_id, round_no)` 冲突时重试一次并报错兜底。
- 同键不同轮 `rows` 可能不一致 → 去重保留最新轮，符合"决策包呈现最终证据集"语义。
- 去重后超 50 条 → 截断保留最新 50 条（不阻塞生成），如需完整证据可看工作区过程。
- `guidance_draft` 与工作区存在 evidence 冗余 → 保留现状，`guidance_draft` 仍是草稿全文历史，工作区是证据增量累积源。

## Migration Plan

- 新增 `V20__business_decision_packet.sql`：建 `task.business_evidence_accum` 与 `task.business_decision_packet` 两张表。
- 回滚：删除两张新表即可，不影响既有表。
