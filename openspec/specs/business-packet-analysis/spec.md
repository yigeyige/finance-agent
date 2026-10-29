# business-packet-analysis Specification

## Purpose
TBD - created by archiving change analyze-frozen-business-evidence. Update Purpose after archive.

## Requirements

### Requirement: Decision packet model analysis
系统 SHALL 根据固定任务信息和已有证据调用模型分析，使用关联场景的 decisionPacketPrompt，未选场景时沿用通用提示。输出 SHALL 包含处理对象（subjectKey）、结论、推荐方案、理由和可选备选方案，允许调整已有初稿的判断。生成阶段 MUST NOT 自动查询数据库补证。

#### Scenario: Generate analysis using scene prompt
- **WHEN** 固定输入包含已有证据和有效场景决策包提示词
- **THEN** 模型接收这些输入并返回处理对象、结论与建议，生成过程不增加数据库工具查询

### Requirement: Basic generation failure handling
无证据、输入超过现有模型上限、调用超时或输出格式非法时，系统 MUST 返回中文错误，不生成正式包。证据 SHALL 由服务端固定输入提供，不接受模型编造或替换证据。

#### Scenario: No evidence
- **WHEN** 固定输入的证据为空
- **THEN** 拒绝分析并提示先完成证据采集

#### Scenario: Model or input fails
- **WHEN** 输入超限、模型失败或输出不符合结构
- **THEN** 返回生成失败，保留已有包，不回退为未经模型分析的装配结果
