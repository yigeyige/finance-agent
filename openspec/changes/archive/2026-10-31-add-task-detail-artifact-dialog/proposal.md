## Why

任务列表进入的只读任务详情页在“决策包”步骤没有流程产物卡片，用户无法查看或重新下载已生成的 BPMN 文件；该能力目前只存在于业务处理向导第三步。

## What Changes

- 在 `TaskDetail.vue` 决策包步骤标题旁增加“流程产物”按钮。
- 点击后打开弹窗，通过现有 `listProcessArtifacts` 加载最近批次。
- 弹窗展示批次、决策包修订、导入状态、待补信息和文件列表。
- 支持重新下载单个文件和 ZIP，复用现有 `downloadProcessArtifact`。

**Non-goals**
- 不在任务详情页生成新流程文件。
- 不改变流程产物后端接口。
- 不改变向导页现有流程文件卡片。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `business-process-artifact`: 只读任务详情页提供流程产物查看与重新下载入口。

## Impact

- 前端：`frontend/src/views/TaskDetail.vue`。
- 复用：`frontend/src/services/process-artifact-api.ts`。
- 验证：`npm --prefix frontend run typecheck` 与 `build`。
