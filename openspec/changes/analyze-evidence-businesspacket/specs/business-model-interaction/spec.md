## ADDED Requirements

### Requirement: Decision packet analysis covers all eligible evidence
系统 SHALL 为业务决策包生成创建专用模型分析 attempt，并根据冻结 manifest 对全部 eligible 证据执行受控分块分析和最终综合。每个分块输出 MUST 通过结构校验并携带 evidence 引用与 digest；最终综合 MUST 覆盖所有已验证分块并区分事实、推断、冲突、缺口、风险、建议、前置条件和需人工确认事项。

#### Scenario: Eligible evidence spans multiple chunks
- **WHEN** 全部 eligible 证据超过单次安全输入预算
- **THEN** 系统按稳定顺序生成多个可验证分块摘要，再基于全部分块完成最终综合

#### Scenario: A chunk cannot be verified
- **WHEN** 任一分块缺失预期 evidence 引用、digest、冲突或 unknown
- **THEN** 专用 attempt 失败且系统不生成业务决策包

#### Scenario: Final synthesis treats inference as fact
- **WHEN** 最终输出无法按结构区分已证实事实与模型推断
- **THEN** 输出校验失败且系统不持久化完成态业务决策包

### Requirement: Decision packet analysis is reproducible
系统 SHALL 为每次成功的业务决策包分析保存冻结 Skill 修订、系统模板版本、manifest digest、分块顺序与摘要 digest、模型显示信息、模式、token 统计和耗时。系统 MUST NOT 保存或返回模型思考过程与原始提示词。

#### Scenario: Operator audits a packet revision
- **WHEN** OPERATOR 查看某个业务决策包修订的技术明细
- **THEN** 系统可证明该修订使用的 Skill、模板、manifest 和分块摘要版本，且不暴露提示正文或思考过程
