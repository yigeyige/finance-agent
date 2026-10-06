# frontend-sse-client Specification

## Purpose
TBD - created by archiving change frontend-api-client. Update Purpose after archive.
## Requirements
### Requirement: Cursor resumable task event stream
SSE 客户端 SHALL 支持以最近事件序列作为 `lastEventId` 游标连接，并在断线后关闭旧连接、使用最新游标显式建立恢复连接。页面 MUST 将可恢复的重放结束或断开表达为等待新进度，不得伪报任务失败。

#### Scenario: Resume after disconnect
- **WHEN** 连接断开且客户端已收到事件序列
- **THEN** 客户端关闭旧连接，且重连 URL 携带最新 `lastEventId`，不会从头消费

#### Scenario: Replay stream completes
- **WHEN** SSE 重放请求因当前没有更多事件而结束或断开
- **THEN** 页面显示等待新进度的中文状态，客户端保留最新游标并尝试恢复

### Requirement: Strict public event parsing
SSE 客户端 SHALL 校验事件名称、事件信封和字段白名单，未知事件或非法 JSON 必须进入错误回调。

#### Scenario: Malformed event
- **WHEN** SSE 数据不是合法公开事件或包含额外字段
- **THEN** 客户端不分发给页面并返回中文解析错误
