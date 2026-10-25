## ADDED Requirements

### Requirement: Evidence context is budgeted by model input capacity
系统 SHALL 根据模型连接的上下文容量、固定系统开销、预留输出和安全余量计算可用输入预算，并对原始切片、分块数量、分块摘要和最终综合分别执行配置化上限。预算 MUST 同时防止 token、字符、行数或条目数任一维度失控。

#### Scenario: Configured budget exceeds provider capacity
- **WHEN** 配置的证据预算大于当前模型连接可安全使用的上下文容量
- **THEN** 系统采用更小的安全预算或拒绝启动调用，且不截断系统安全约束

#### Scenario: Raw evidence slice reaches a limit
- **WHEN** 某证据的允许行数、总行数、字符数或估算 token 达到配置上限
- **THEN** 系统确定性停止扩展切片并在 manifest 中记录截取与 unknown，而不是静默宣称完整

### Requirement: Chunk summaries remain reference-only and complete
系统 SHALL 要求每个分块摘要只引用该分块中已注册的 evidence ID 和 digest，并输出事实摘要、冲突、缺口和 unknown。最终综合 MUST 覆盖全部已验证分块摘要；缺失任一分块或 unknown 时校验失败。

#### Scenario: Chunk summary omits an expected unknown
- **WHEN** 分块摘要没有返回 manifest 声明的 unknown
- **THEN** 压缩校验失败且该摘要不得进入最终综合

#### Scenario: Final synthesis omits a chunk
- **WHEN** 最终综合输入或输出缺少任何一个已验证分块引用
- **THEN** 系统拒绝将综合结果标记为完整分析
