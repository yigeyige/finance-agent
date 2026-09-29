## Context

覆盖结果的完整性与事实来源是决策包可信度的边界。结构校验只保证章节存在，不能判断章节中的结论是否与覆盖状态匹配，因此增加独立、无副作用的领域校验器，避免把业务语义塞入 JSON schema 或控制器。

## Decisions

1. `PacketWordingValidator` 接收已经确定的 `CoverageCompleteness` 和业务影响确认状态，不重新枚举来源、不调用模型、不读取数据库。
2. `PARTIAL`/`UNKNOWN` 必须有非空 `limitations`，并拒绝“全量/全部/所有对象/不存在遗漏”等绝对覆盖措辞；`UNKNOWN` 还必须有 `facts.undetermined`。
3. `facts.current` 中标记为 `RAG`、`MEMORY` 或 `MODEL` 的条目一律拒绝；这些内容只能进入 `undetermined` 或限制说明。
4. 业务影响未确认时，`businessConfirmations.pendingItems` 必须非空，且 `recommendations`/`computations` 不得写出“实际影响、已造成影响、影响金额”等确定性结论。
5. fixture 和脚本只使用合成占位，默认离线运行；用户审阅时保存每个场景的通过/拒绝结果。
