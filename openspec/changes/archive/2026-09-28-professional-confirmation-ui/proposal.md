## Why

证据页面已经能展示发现，但专业判断仍缺少逐条确认入口，无法记录理由、证据引用、确认人和范围。没有确认审计，专业判断不能安全进入后续决策包。

## What Changes

- 新增逐 finding 的专业确认 API，支持确认、拒绝和需要更多证据三种决定。
- 新增确认记录和 finding 状态更新，校验任务归属、范围修订、重复确认和确定性结果保护。
- 在证据页面提供逐条专业确认表单，展示已确认结果和确认审计元数据。

## Capabilities

### New Capabilities
- `professional-confirmation-ui`: 专业确认 API、页面和审计。

### Modified Capabilities

## Impact

- 影响 evidence 应用服务、finding 状态更新、专业确认 REST 响应及审计事件。
- 影响证据总览响应、前端确认类型、服务和发现页面。
- 不新增依赖，不允许通过该入口修改确定性 finding。
