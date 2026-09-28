# Design: HarnessAgent 最小冒烟

`HarnessAgentFactory` 维护可注入的 `AgentStateStore`，创建时强制校验安全的 `HarnessIdentity`，并把 identity 的 session、workspace 和状态存储绑定到 AgentScope HarnessAgent。默认使用 `InMemoryAgentStateStore` 和 `StubAgentScopeModel`，关闭文件、shell、动态 skills/subagents 与 transcript，确保测试不会访问网络或产生不可控副作用。

测试通过同一 `RuntimeContext` 连续调用两次，验证 Stub 响应、会话身份和构建的 workspace；不安全或缺失身份在构建前失败。生产 PostgreSQL 状态持久化由后续任务完成。
