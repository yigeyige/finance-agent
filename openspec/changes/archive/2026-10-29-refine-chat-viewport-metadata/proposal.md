## Why

聊天面板的固定最大高度导致高屏底部留白，输入与按钮分两行占用空间；业务处理页缺少标题，顶部等待状态缺少信息增量。需要简洁展示当前模型与可信调用元数据。

## What Changes

- 添加“业务处理”页标题，聊天根据实际顶部位置填充剩余视口。
- 输入与“发送”按钮同排，移除空闲等待状态。
- 复用建议响应 metadata 展示实际模型、最近调用 Token 和耗时。
- 新增只读模型运行摘要 API，为 OPERATOR/管理员展示当前模式与模型名称，版本化契约。

## Capabilities

### New Capabilities
- `chat-viewport-model-summary`: 聊天视口、自适应输入区和安全模型摘要。

### Modified Capabilities
无。

## Impact

涉及 WorkbenchHome、WorkbenchChat、model-api、生成类型、模型摘要后端应用服务和控制器、OpenAPI/JSON Schema、测试。不改变调用链、Token 计费或权限配置，不增加依赖和数据库迁移。
