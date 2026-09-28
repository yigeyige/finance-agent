## ADDED Requirements

### Requirement: Controlled test identity adapter
local/test SHALL expose exactly OPERATOR、VIEWER、PLATFORM_ADMIN 三个合成身份，并通过当前身份接口返回用户和角色。

#### Scenario: Switch synthetic identity
- **WHEN** local/test 请求选择白名单身份
- **THEN** 当前身份接口返回对应合成用户和角色

#### Scenario: Production rejects test identity
- **WHEN** prod 请求携带测试身份标识或启用测试种子
- **THEN** 启动或请求被拒绝，不建立测试身份上下文

### Requirement: Server derives actor from context
业务写请求 SHALL 从当前身份上下文取得操作者，不信任请求体或页面自报的用户标识。

#### Scenario: Missing identity context
- **WHEN** 写请求没有有效 Principal 或受控测试身份
- **THEN** 服务端返回拒绝，不执行领域动作
