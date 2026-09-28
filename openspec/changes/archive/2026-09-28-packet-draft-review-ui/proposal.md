## Why

决策包草稿已经可以生成，但还缺少准确性复核入口。复核必须绑定 draft、内容摘要、范围修订和运行版本，并在专业确认未完成或任一绑定不一致时阻止批准。

## What Changes

- 新增草稿复核应用服务和 REST API，支持批准/退回并追加审计记录。
- 复核响应回显被复核摘要、范围、运行版本、复核人和时间。
- 在草稿 JSON/HTML 预览页面增加逐草稿复核表单。

## Capabilities

### New Capabilities
- `packet-draft-review-ui`: 决策包草稿准确性复核。

### Modified Capabilities

## Impact

- 影响 packet review 应用服务、复核 API、错误映射和前端草稿页面。
- 复用 Finding/ProfessionalConfirmation 仓储，预留双人策略但首期允许同一操作人发起和复核。
