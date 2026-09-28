## Decisions

1. `FieldAccessPolicy` 明确 read、retain、model 三个集合，retain/model 必须是 read 子集。
2. `TrustedFieldProjector` 只接受 JSON object；源返回包含任一未授权顶层字段即 fail closed，然后分别生成留存和送模投影。
3. `SensitiveFieldScanner` 递归拒绝原响应、凭证、Token、prompt 和 reasoning 等键；投影输出仍需扫描。

## Non-Goals

- 不改变数据库表结构；具体证据写入在后续任务复用投影结果。
- 不把原响应写入日志、异常、缓存或 checkpoint。

