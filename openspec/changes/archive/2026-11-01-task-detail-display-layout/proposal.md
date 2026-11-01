## Why

任务详情只显示发起人内部 ID，缺少业务场景和数据库实例，影响用户核对任务上下文。只读业务说明布局与创建模式不一致，决策包按钮也与下方卡片过近。

## What Changes

- 详情响应增加发起人名称、业务场景名称、数据库实例名称等只读展示字段。
- 业务说明页签按创建页的字段顺序和双列节奏展示只读信息。
- 决策包标题栏与卡片之间增加间距。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `three-step-business-workbench`: 明确任务详情业务说明字段与布局，以及决策包标题栏间距。

## Impact

影响任务详情服务、详情响应契约、前端解析与 TaskDetail 页面；不增加依赖和写入行为。
