## ADDED Requirements

### Requirement: Latest exception snapshot

系统 SHALL 在单实例内存中仅保留最近一次未预期业务接口异常或最终失败的任意模型交互异常的不可变快照，后发生的符合范围异常 MUST 覆盖前一条。快照 SHALL 包含发生时间、来源、异常类型和有界堆栈帧，MUST NOT 保留 `Throwable`、原始提示词、模型输出、SQL、密钥、连接串或真实用户数据。普通业务拒绝与内部恢复的模型异常 MUST NOT 覆盖快照。服务重启后快照为空。

#### Scenario: Unexpected API failure
- **WHEN** 业务接口抛出未预期服务端异常
- **THEN** 系统保存该异常的受限堆栈快照且不改变原有错误响应

#### Scenario: Final model failure
- **WHEN** 任一模型交互最终向调用方抛出异常
- **THEN** 系统以该异常覆盖最近快照，不限具体 HTTP 错误分类

#### Scenario: Recovered model failure
- **WHEN** 模型客户端内部失败后恢复并向调用方返回结果
- **THEN** 最近快照保持不变

#### Scenario: Sensitive data in cause message
- **WHEN** 异常消息链包含提示词、密钥、连接串或业务数据
- **THEN** 快照仅保留类型及堆栈帧，不包含原始消息

### Requirement: Administrator exception page

系统 SHALL 在左侧菜单提供仅 `PLATFORM_ADMIN` 可见的“异常堆栈”页面，进入时仅请求一次最近快照。只读接口 MUST 在服务端校验 `PLATFORM_ADMIN`，其他身份不得读取。页面 SHALL 显示异常时间、来源、类型与堆栈，且 MUST 提供无记录和读取失败状态；页面 MUST NOT 自动轮询或订阅推送。

#### Scenario: Administrator opens page
- **WHEN** 平台管理员进入异常堆栈页面且内存已有快照
- **THEN** 页面读取一次并展示该快照

#### Scenario: No exception recorded
- **WHEN** 平台管理员进入页面且内存尚无快照
- **THEN** 页面展示中文空状态

#### Scenario: Unauthorized read
- **WHEN** 非平台管理员请求最近异常接口或访问页面
- **THEN** 服务端拒绝读取，前端也不展示菜单并阻止进入页面
