## Why

任务创建第一步上传的附件目前只在首次模型分析中临时使用，不持久化，模型调用结束后无法追溯，也无法证明模型实际看到了什么内容。

## What Changes

- 新增 `task.task_attachment` 表，保存任务附件原始字节、文本投影、SHA-256、大小、类型、截断标记和上传时间。
- 首次带附件分析时，在调用模型前将附件写入任务附件库；落库失败则不继续模型分析。
- 同一任务内按 SHA-256 去重。
- 提供任务附件列表和原始文件下载接口。

**Non-goals**
- 不在后续修改轮次自动重复注入历史附件。
- 不改变现有附件格式、数量和大小限制。
- 不新增前端附件库页面（本次先提供后端能力，UI 可后续接入）。

## Capabilities

### New Capabilities

- `task-attachment-archive`: 任务附件留痕、去重、列表和下载。

### Modified Capabilities

- `business-handling-guidance`: 首次带附件分析前先落库留痕。

## Impact

- 后端：新增 Entity/Mapper/Repository/Service/Controller；修改 `BusinessModelController`。
- 数据：Flyway V34 新增 `task.task_attachment`。
- 测试：附件落库、去重、文本投影一致性和下载。
