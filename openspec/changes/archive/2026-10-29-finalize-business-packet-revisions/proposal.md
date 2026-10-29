## Why

模型分析后需要保存业务决策包，并落实「已生成后任务信息不可修改，仍可补证再生成」的规则。

## What Changes

- 组装本次生成输入：从服务端读取任务信息（title、originalQuestion）、已有累积证据与本次决策包提示词，分析、保存复用同一份输入，不中途重读。
- 将模型分析结果（subjectKey、conclusion、recommendedGoal、alternativeGoals、reason）与场景/实例留痕、证据保存为业务决策包。
- content 契约：`packetId/taskId/revision/createdAt` + 模型结果 + `sceneSkillId/Name` + `databaseConfigId/Name` + `evidence`；不存 `title/originalQuestion`，去掉 `objectKey/statement`。
- 沿用现有 revision、内容摘要和历史读取，变化新增修订，旧包只读保留。
- 首次成功生成后禁止修改任务信息（originalQuestion、businessSceneSkillId、databaseConfigId）；失败不锁定。

## Capabilities

### New Capabilities
- `business-packet-task-lock`: 包生成后的任务信息只读规则。

### Modified Capabilities
- `business-decision-packet`: 固定输入经模型分析后保存修订。

## Impact

依赖 `analyze-frozen-business-evidence`，修改现有生成服务和任务更新校验；复用现有修订存储，不接复核、发布或交接流程。
