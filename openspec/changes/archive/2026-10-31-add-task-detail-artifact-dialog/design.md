## Context

`TaskDetail.vue` 是任务列表进入的只读详情页，三步切换展示业务说明、模型协作和决策包。流程产物接口已存在：`listProcessArtifacts(taskId)` 返回最近两批，`downloadProcessArtifact` 支持单文件和 ZIP。

## Decisions

### 1. 入口位置

在决策包卡片标题“业务决策包”右侧增加“流程产物”按钮，点击打开弹窗；不新增步骤，避免破坏现有三步结构。

### 2. 弹窗内容

- 使用 `el-dialog`，打开时调用 `listProcessArtifacts`。
- 每个批次展示：批次标识前 8 位、决策包修订、导入状态、生成时间、待补信息、文件列表。
- 每个文件提供“重新下载”；导入候选批次提供“下载 ZIP”。
- 空批次显示“暂无流程产物”。
- 加载和下载分别有 loading，失败复用页面 `errorMessage`。

### 3. 复用现有 API

直接复用 `ProcessArtifactBatch`、`listProcessArtifacts`、`downloadProcessArtifact`，不新增后端接口。

## Risks

- 任务详情页为只读页，弹窗只下载不生成，避免误触发模型调用。
- 旧批次可能基于旧决策包，展示修订号供用户判断。
