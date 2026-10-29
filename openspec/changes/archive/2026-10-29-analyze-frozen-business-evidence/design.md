## Context

以固定的任务信息、已有证据和场景决策包提示词为输入，补上决策包生成时的模型分析；处理对象由模型分析得出，不从任务前提读取。

## Goals / Non-Goals

**Goals:** 消费决策包提示词，分析已有证据，返回含处理对象的结论与建议。
**Non-Goals:** 暂不做冲突/缺口识别、覆盖度、证据资格、分块摘要或调用治理，不自动补证。

## Decisions

- 复用现有模型客户端增加专用方法，输入为任务信息、已有证据和 decisionPacketPrompt。
- 输出使用新建的精简 record `BusinessPacketAnalysisSuggestion`（subjectKey、conclusion、recommendedGoal、reason 及可选 alternativeGoals 共 5 字段），不复用 `BusinessHandlingDraft`，最小化模型 token；subjectKey 由模型分析得出。
- 未选 Skill 时沿用通用提示；已选场景失效则提示错误。读取到的提示只用于本次调用，不建立提示版本系统。
- 证据由服务端固定输入绑定，不接受模型另造证据。生成阶段不给数据库查询工具。
- 沿用现有超时、模型模式元数据及结构校验，保留必要输入长度上限；超限直接报错，不在本批建设分块流水线。

## Risks / Trade-offs

单次调用支持的证据量受模型输入上限限制；超限时用户需减少本次调查范围。

## Migration Plan

接入专用模型方法，保持现有调查模型接口不变。使用合成数据/Stub 验证基础成功和失败路径，不把 Stub 测试当作真实模型验收。

## Open Questions

无。
