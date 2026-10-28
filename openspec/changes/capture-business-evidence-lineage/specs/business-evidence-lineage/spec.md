## ADDED Requirements

### Requirement: Fixed generation input
系统 SHALL 在生成开始时，从服务端读取已保存任务信息、已有累积证据和本次使用的决策包提示词，组成固定输入。后续分析和保存 MUST 使用同一份输入，不依赖浏览器提交任意证据正文。

#### Scenario: Evidence added after input is fixed
- **WHEN** 本次输入已固定后又有新证据写入
- **THEN** 本次输入保持不变，新证据留到下一次生成使用

#### Scenario: Task or prompt changes during analysis
- **WHEN** 输入固定后任务信息或提示配置发生变化
- **THEN** 本次分析和保存仍使用开始时读取的内容，不中途混入新内容
