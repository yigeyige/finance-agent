# latest-exception-stack-view Specification

## Purpose

平台用户查看本人、管理员查看全站最近一次未预期业务接口异常或最终模型交互异常的安全堆栈快照，以便定位出错的代码位置。记录仅保存在内存中，服务重启后清空。

## Requirements

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

### Requirement: Per-user latest snapshot
系统 SHALL 在当前进程内按可信用户标识保存最近异常快照；后台模型失败 SHALL 通过可信任务发起人归属，不保存原始异常消息、提示词、密钥、SQL 或真实用户数据。无法归属用户的异常 MUST NOT 出现在普通用户快照中。服务重启后记录清空。

#### Scenario: Different users fail
- **WHEN** 两名用户先后产生未预期异常
- **THEN** 每人只读取自己的最近异常，管理员读取全站最后一条
