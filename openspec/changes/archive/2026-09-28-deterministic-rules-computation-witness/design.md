## Context

快照域已经保存获准字段、结果摘要和生效分类，计算见证域已经保存规则版本、输入/输出摘要和复算状态。需要在应用层建立一个小型规则 SPI，保证规则代码可版本化、输入可规范化、复算可以脱离首次执行独立运行。

## Goals / Non-Goals

**Goals:**

- 规则只接收经过安全扫描和 canonical JSON 规范化的获准快照。
- 首个人员引用规则输出确定性 `Finding`，同时生成不可覆盖旧结果的见证对象。
- 复算只比较规则版本、算法版本、快照集合和输入/输出摘要，未知版本 fail-closed。

**Non-Goals:**

- 不接入模型、不修改快照和见证表结构、不提供通用表达式语言。
- 不在本任务中持久化执行结果；由既有 Repository 端口负责后续写入。

## Decisions

- **Java SPI 而非脚本规则**：使用泛型 `DeterministicRule` 接口和显式版本字符串，编译期约束更适合当前 Java 17 基线。
- **复用安全组件**：输入使用现有 `SensitiveFieldScanner` 和 `CanonicalJsonCodec`，不手写 JSON/摘要能力。
- **首个规则输入最小化**：人员引用规则只读取获准快照中的 `classification` 和引用标识字段，未知字段不会进入规则。
- **独立复算服务**：`RuleRecomputeService` 通过规则目录按版本解析规则，任何缺少快照或版本不匹配都返回非 MATCHED 状态。

## Risks / Trade-offs

- [Risk] 规则版本注册遗漏 → 复算返回 `UNAVAILABLE`，不能确认确定性判断。
- [Risk] 获准快照字段顺序变化 → canonical JSON 排序后摘要保持稳定。
- [Risk] 规则输出语义扩展 → 通过新的 ruleSetVersion 生成新见证，不覆盖旧记录。

## Migration Plan

无需数据库迁移。先通过单元测试固化黄金向量，未来由调用方将 `RuleEvaluationResult.witness()` 交给现有见证仓储。

## Open Questions

无。
