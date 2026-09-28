## Context

现有 `ConfigSourceAdapter` 为合成场景专用方法，真实源接入若直接复制接口会造成契约分叉。任务要求先稳定查询、分页和复查结果对象，并在 prod profile 下明确阻断未配置来源。

## Goals / Non-Goals

**Goals:**

- 固化适配器读取端口和不可变 DTO，供未来 API/只读视图适配器通过同一 TCK。
- 保留 `SyntheticConfigSourceAdapter` 离线测试能力，同时增加通用 `ApprovedQuery` 入口。
- 生产未配置时抛出带稳定错误码的异常，绝不伪造目录或数据。

**Non-Goals:**

- 不连接真实 API、数据库视图或认证系统。
- 不实现字段映射、重试和分页业务逻辑；这些由后续 P1 任务负责。

## Decisions

- **兼容扩展**：保留旧 `SourceQuery`/`SourcePage` 方法，新增稳定 SPI 方法，避免破坏已有合成测试。
- **显式未配置异常**：使用项目内错误码异常，不返回 null 或空页，便于上层 fail-closed。
- **prod profile 占位**：生产适配器只在 `prod` profile 激活，开发 profile 继续使用合成源。
- **契约模型最小化**：`ApprovedQuery` 携带目录版本，`ScopePage` 携带页/水位/摘要元数据，`RecheckResult` 仅表达状态和结果摘要。

## Risks / Trade-offs

- [Risk] 新 SPI 与旧合成方法并存一段时间 → 通过契约测试约束两者均为只读，后续真实适配器只实现新入口。
- [Risk] prod 未配置导致启动后首次调用失败 → 错误码明确标记 P1 阻断，不允许静默降级。

## Migration Plan

无需迁移。未来真实适配器实现 `ConfigSourceAdapter.query(ApprovedQuery)` 并提供真实 `SourceCatalog` 后即可替换占位。

## Open Questions

无。
