# Design: PostgreSQL Harness 状态适配

`PostgresAgentStateStoreAdapter` 通过 `SessionStateRepository.findBySessionIdAndUserId` 解析业务 task，再使用完整 user/session/task 边界更新 `session_state`。AgentScope 的 agentId 与 userId 必须是同一个 UUID，避免匿名或跨用户读取。

`JacksonAgentStateCodec` 复用 Jackson 将 `State` 转为 `agentscope-state-v1` JSON，写入 SHA-256 摘要；读取时拒绝未知 schema、摘要不匹配、旧 `harnessVersion` 和无法反序列化的状态。列表和删除均通过仓储边界执行，未绑定的 session 不会被隐式创建或恢复。
