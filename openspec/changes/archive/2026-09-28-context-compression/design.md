## Context

Context Compression 是模型调用前的受限摘要边界。它只能引用当前任务已经存在的对象，保留关键未知项，并把摘要与证据引用分开；压缩失败时必须阻止继续送模，由上层转人工缩小范围。

## Goals / Non-Goals

**Goals:**

- 以类型化引用和摘要字段形成可验证的最小输出。
- 以 authoritative reference catalog 校验每个引用的存在和 digest。
- 保留未知项集合，禁止摘要文本声明新的 evidence/finding。
- 使用确定性长度与字段白名单，保证压缩结果可审计、可测试。

**Non-Goals:**

- 不调用模型生成压缩摘要，不做 RAG、向量检索或事实推断。
- 不复制 snapshot 原文，不新增持久化表，不将摘要升级为事实或证据。

## Decisions

- `CompressionReference` 只包含类型、ID 和 digest；类型白名单为 TASK、SCOPE、PREMISE、EVIDENCE、FINDING、CURRENT_STEP。
- `ContextCompressionSummary` 将引用、按引用 ID 索引的短摘要和未知项分开存储；摘要只允许引用已登记 ID，不能携带新的证据 ID。
- `CompressionReferenceCatalog` 是调用方提供的只读 `Map<id, digest>`，校验器同时检查引用类型和 digest，避免信任模型输出。
- `ContextCompressionService` 只做字段裁剪和长度限制，不负责生成事实；任何校验失败都抛出 `ContextCompressionException`，由上层进入人工缩小范围。
- 复用 `Sha256Digest`/`CanonicalJsonCodec` 计算压缩输入摘要，不保存原始长会话内容。

## Risks / Trade-offs

- [Risk] 过严的摘要长度导致有用上下文丢失 → [Mitigation] 通过配置显式调大上限，失败仍转人工而不是静默截断。
- [Risk] 调用方把摘要 ID 当作证据 ID → [Mitigation] schema 将摘要 key 限定为已登记引用，且 validator 不允许摘要新增引用。
- [Risk] 未知项被模型省略 → [Mitigation] validator 对比 expected unknown set，缺失即失败。

## Migration Plan

无数据库迁移。先运行离线契约测试和 Maven 全量验证，再由用户在长会话中人工比较压缩前后引用与未知项；失败路径保持人工缩小范围。
