## Why

P1 已明确只使用测试业务源，不能再以真实 SSO、真实人员编号或 break-glass 作为认证适配器前置条件。仓库已有 local/test 受控身份解析与角色绑定能力，但当前 P1 任务尚未把这套能力作为 P1 测试认证适配器完成验收，也缺少对未知身份和已撤销角色的直接验证证据。

## What Changes

- 复用现有 `IdentityResolver`、`IdentityContextFilter` 和合成身份种子，确认其作为 P1 测试认证适配器的唯一实现，不新增 SSO、JWT、Token 验证或外部依赖。
- 补充受控测试身份白名单、prod 隔离、未知身份失败关闭和已撤销角色不生效的自动化验证。
- 更新 `DEV-P1-003` 的测试范围、配置说明和操作文档，明确测试身份只适用于 local/test，真实统一认证继续作为 P2 独立事项。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `test-identity-context`：补充未知测试身份失败关闭和撤销角色不进入当前身份的可验证要求。

## Impact

- 后端测试：新增或扩展 IAM 应用层测试；不改变 API、数据库结构、运行配置或生产认证路径。
- 文档与任务：更新 P1-003 任务卡及测试认证适配器操作说明。
- 依赖：无新增依赖；继续使用当前 Spring、JUnit 与 Mockito 测试能力。
