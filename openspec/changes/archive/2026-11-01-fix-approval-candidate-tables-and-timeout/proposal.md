## Why

审批流程调查时，schema 搜索只召回了空的 `psc_act_re_model`，没有召回 `act_re_model`/`act_re_procdef`；提示词扩写后超过领域 2000 字符限制，导致 400 MODEL_CONTEXT_INVALID；模型网关偶发 504。

## What Changes

- 审批流程场景 schema 搜索加入核心表提示。
- `investigationPrompt` 校验上限从 2000 提升到 8000。
- V40 增加标题兜底流程名、前置表为空停止下游查询规则。
- 模型网关 504 重试一次。

## Capabilities

### Modified Capabilities

- `ems-database-connection-analysis`: 审批流程候选表召回和前置表链式约束。
- `business-handling-guidance`: 长调查提示词和标题兜底。

## Impact

- 后端：`EmsDatabaseAssistanceService`、`DeepSeekBusinessModelClient`、`BusinessSceneSkill`、`BusinessSceneSkillRequest`。
- 数据：Flyway V40。
- 验证：guidance 返回 200，证据包含 `act_re_model`。
