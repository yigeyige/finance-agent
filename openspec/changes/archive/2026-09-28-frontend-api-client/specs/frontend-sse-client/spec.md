## ADDED Requirements

### Requirement: Cursor resumable task event stream
SSE 客户端 SHALL 支持以最近事件序列作为 `lastEventId` 游标连接，并在断线后使用最新游标恢复。

#### Scenario: Resume after disconnect
- **WHEN** 连接断开且客户端已收到事件序列
- **THEN** 客户端重连 URL 携带最新 `lastEventId`，不会从头消费

### Requirement: Strict public event parsing
SSE 客户端 SHALL 校验事件名称、事件信封和字段白名单，未知事件或非法 JSON 必须进入错误回调。

#### Scenario: Malformed event
- **WHEN** SSE 数据不是合法公开事件或包含额外字段
- **THEN** 客户端不分发给页面并返回中文解析错误
