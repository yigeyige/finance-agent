## Context

`SyntheticConfigSourceAdapter` 已返回页号、是否有下一页、来源版本和页摘要；证据域已存在 `CoverageRecord` 与 `CoverageCompleteness`。当前缺少跨页一致性校验和将遍历结果转换为覆盖证据的应用服务。实现必须兼容 Java 17、保持分层边界，并优先复用已有 SHA-256 工具。

## Goals / Non-Goals

**Goals:**

- 用可注入的分页获取端口实现有界、可重复的分页枚举。
- 对分页顺序、预期总量、水位、路径和失败状态进行 fail-closed 校验。
- 生成可持久化的 `CoverageRecord`，使 COMPLETE/PARTIAL/UNKNOWN 可追溯。

**Non-Goals:**

- 不改造现有来源适配器的查询协议。
- 不新增数据库表、迁移、外部依赖或真实系统接入。
- 不在本任务中实现跨源冲突分类或确定性业务规则。

## Decisions

- **独立分页结果模型**：新增 `CoveragePage`，避免修改 `SourcePage` 构造契约并保留来源适配器兼容性。
- **函数式获取端口**：枚举器接收 `PageFetcher`，测试和未来来源适配器可替换，不依赖具体基础设施类。
- **保守分类**：完整遍历且计数/水位/路径均一致才为 `COMPLETE`；可识别缺页或来源失败为 `PARTIAL`；重复 token、无法确定一致性或异常状态为 `UNKNOWN`。
- **稳定摘要**：按页号和规范化记录文本拼接后调用现有 `Sha256Digest.hex`，避免自定义摘要实现。
- **证据工厂**：`CoverageRecordFactory` 仅负责领域记录组装，时间和身份由调用方传入，避免隐式上下文。

## Risks / Trade-offs

- [Risk] 来源不提供预期总量时无法宣称完整覆盖 → 结果降为 `UNKNOWN`，并保留失败原因。
- [Risk] 恶意或异常来源持续返回下一页 → 使用最大页数和页号/token 单调性守卫终止。
- [Risk] 记录 Map 顺序影响摘要 → 使用排序后的键值规范化文本生成摘要。

## Migration Plan

无需数据库迁移。实现后由单元测试和 Maven verify 验证；未来接入真实来源时只需实现 `PageFetcher` 并映射来源元数据。

## Open Questions

无。
