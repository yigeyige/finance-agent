## Context

`DeepSeekOpenAiAdapter` 已通过 AgentScope 2.0.3 OpenAI 扩展负责 Chat Completions、工具和结构化输出。DeepSeek 的 reasoning 内容在工具循环中需要继续传递给模型，但其唯一允许的生命周期是当前 Reactor 调用；仓库中的事件、日志、异常、checkpoint、数据库和 SSE 都不应接收该字段。

## Goals / Non-Goals

**Goals:**

- 将 thinking high 和 `reasoning_content` 的输入/输出协议映射收敛在适配层。
- 用不可序列化的瞬时容器承载 reasoning，并在成功、失败、取消和完成时清理。
- 提供统一过滤器，移除消息、事件、异常、日志和持久化 payload 中的 reasoning 字段。
- 以 HTTP Stub 和唯一标记扫描证明禁止字段不出边界。

**Non-Goals:**

- 不把 reasoning 写入数据库、checkpoint、日志或客户端；不提供恢复 reasoning 的能力。
- 不增加 HTTP/JSON 库或替换 AgentScope；不执行真实 DeepSeek live-test。
- 不改变最终回答、工具调用参数和非敏感模型元数据契约。

## Decisions

- `ThinkingContentFilter` 使用递归 Jackson `JsonNode` 过滤 `reasoning_content`、`reasoningContent` 和 `thinking` 键；过滤结果深拷贝，输入不被修改。
- `EphemeralThinkingContext` 只保存当前调用的 `String` 值和生命周期状态，不实现 `Serializable`，`close()` 后读取和写入都拒绝或返回空值。
- `DeepSeekOpenAiAdapter` 的默认 `GenerateOptions` 设置 `thinkingBudget`/`reasoningEffort` 为 high，并由 `ThinkingContentFilter` 在输出映射前移除 reasoning；模型错误直接传播，不做 fallback。
- 事件/持久化边界统一调用过滤器，测试使用唯一 reasoning 标记扫描 JSON、异常文本和模拟 SSE，避免把过滤逻辑散落在多个调用点。

## Risks / Trade-offs

- [Risk] 第三方响应可能使用新的 reasoning 字段名 → [Mitigation] 保留固定禁止字段集合并由协议契约测试捕获未知字段，新增名称需显式评审。
- [Risk] 过滤器误删业务字段 → [Mitigation] 只匹配完整键名，不匹配普通文本内容，并覆盖深层对象/数组测试。
- [Risk] 调用中途取消时容器未清理 → [Mitigation] 适配器通过 `Flux.using` 绑定 close 生命周期，并测试 cancel/error/complete 三条路径。

## Migration Plan

默认 local/test 继续使用 Stub，无数据迁移。发布时先运行离线契约和全量 Maven 验证；真实模型验证仍由用户手动设置 key 并触发 live profile。回滚只需恢复适配器版本，不涉及数据库迁移。

## Open Questions

无；未知 reasoning 字段名不自动兼容，需通过后续任务评审后加入禁止集合。
