# Proposal

## Why

业务处理过程中，每轮模型思考产生的关联证据没有跨轮累积——`guideBusinessHandling` 每次调用都会重新采集证据（从空列表开始），前端只展示最新一轮。管理员需要一份"业务决策包"，把多轮累积证据与业务处理结论一次性固化、可查看、可追溯。

## What Changes

- 新增**证据工作区**：每次业务处理（`guideBusinessHandling`）把当轮证据追加保存为只读留痕（append-only、带轮次），随任务存续不自动清空。
- 新增**业务决策包**：管理员一键生成，读取证据工作区累积结果与最新业务处理草稿，evidence 按 `(schema, table, purpose)` 去重保留最新，落库并 revision 递增、旧版保留。
- 修改**任务详情证据展示**：由"最新草稿的关联证据"改为"累积证据视图"，并新增"生成业务决策包"入口。
- 前端：业务处理页证据面板切累积视图，新增生成按钮与决策包查看。

## Capabilities

### New Capabilities

- `business-decision-packet`: 业务处理证据跨轮累积与一键生成可查看的业务决策包。

### Modified Capabilities

- `guidance-draft-persistence`: 任务详情的证据展示从"最新草稿的关联证据"改为"累积证据"，并支持生成业务决策包。

## Impact

- 后端：`BusinessModelInteractionService`（写入证据工作区）；新增证据累积与业务决策包的 domain / application / infrastructure / web 各一层；新增数据库迁移。
- 前端：`WorkbenchHome.vue`、`services/model-api.ts`、`api/generated/index.ts`。
- 数据库：新增证据累积表与业务决策包表（不改动现有 `guidance_draft` 与 `packet` 表）。
