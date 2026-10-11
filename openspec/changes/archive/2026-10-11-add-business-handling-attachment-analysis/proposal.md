## Why

业务人员在新建业务处理时往往拥有补充材料，而当前模型初稿只基于标题和业务说明，无法利用这些材料。需要在不持久化原始文件、不让浏览器任意拼接模型上下文的前提下，安全地将受限附件内容交给模型辅助分析。

## What Changes

- 在新建业务处理表单提供受限附件的选择、展示和移除能力。
- 新增初稿生成的 multipart 接口；后端校验附件数量、大小、格式和 UTF-8 内容，将受限文本投影加入模型上下文。
- 保持未上传附件时既有初稿 API 和人工确认边界不变；不保存原始附件、解析文本或模型提示词。

## Capabilities

### New Capabilities

- `business-handling-attachment-analysis`: 定义新建业务处理附件的上传限制、受控文本投影和安全失败边界。

### Modified Capabilities

- `business-model-interaction`: 扩展受控模型初稿调用，使其能接收后端校验后的附件文本投影。
- `business-handling-guidance`: 扩展业务处理向导的初稿生成流程，支持用户选择附件并维持可编辑、人工确认的处理边界。

## Impact

- 前端调整业务处理向导和模型 API service，使用 Element UI 上传控件及 multipart 请求。
- 后端调整模型交互控制器、应用服务、上下文、模型客户端并新增受限附件解析组件和测试。
- 不新增第三方依赖、不修改数据库结构，也不持久化原始附件。
