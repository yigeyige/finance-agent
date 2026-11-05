## MODIFIED Requirements

### Requirement: Administrator exception page
系统 SHALL 在左侧菜单向 PLATFORM_USER 和 PLATFORM_ADMIN 提供“异常堆栈”页面，进入时仅请求一次最近快照。只读接口 MUST 在服务端校验可信身份：普通用户只能读取归属自己的最近快照，管理员读取全站最近快照，包括无法归属用户的后台异常。页面 SHALL 显示异常时间、来源、类型与有界堆栈，并提供无记录和读取失败状态；页面 MUST NOT 自动轮询或订阅推送。

#### Scenario: Administrator opens page
- **WHEN** 平台管理员进入异常堆栈页面且内存已有快照
- **THEN** 页面读取一次并展示全站最近快照

#### Scenario: Ordinary user opens page
- **WHEN** 普通用户进入异常堆栈页面
- **THEN** 页面只展示该用户产生的最近快照，其他用户和后台异常不可见

#### Scenario: No exception recorded
- **WHEN** 当前可见范围内尚无异常
- **THEN** 页面展示中文空状态

#### Scenario: Unauthenticated read
- **WHEN** 无有效身份请求最近异常接口或访问页面
- **THEN** 服务端拒绝读取，前端跳转登录页

## ADDED Requirements

### Requirement: Per-user latest snapshot
系统 SHALL 在当前进程内按可信用户标识保存最近异常快照；后台模型失败 SHALL 通过可信任务发起人归属，不保存原始异常消息、提示词、密钥、SQL 或真实用户数据。无法归属用户的异常 MUST NOT 出现在普通用户快照中。服务重启后记录清空。

#### Scenario: Different users fail
- **WHEN** 两名用户先后产生未预期异常
- **THEN** 每人只读取自己的最近异常，管理员读取全站最后一条
