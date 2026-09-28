## ADDED Requirements

### Requirement: Local HarnessAgent smoke boundary

系统 SHALL 通过显式 `userId` 和 `sessionId` 创建 AgentScope Java 2.0.3 `HarnessAgent`，绑定本地 workspace、可替换状态存储和离线 Stub 模型，且默认构建/测试不访问网络。

#### Scenario: Same identity continues a session
- **WHEN** 同一用户和 session 使用同一 RuntimeContext 连续调用
- **THEN** 两次调用均完成并使用同一会话边界与事件流

#### Scenario: Identity and workspace are isolated
- **WHEN** userId/sessionId 缺失、不安全或 workspace 路径越界
- **THEN** Agent 创建前失败，不允许跨会话读取或越界访问

### Requirement: Smoke evidence is not production persistence

系统 SHALL 明确本冒烟仅证明 HarnessAgent、Stub、workspace 和内存状态存储的兼容性，不把内存状态视为生产持久化完成。

#### Scenario: Local stub invocation
- **WHEN** 冒烟入口调用本地 Stub 模型
- **THEN** 产生可观察的调用完成结果，且不创建真实模型网络连接
