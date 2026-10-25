## ADDED Requirements

### Requirement: Multi-round guidance uses bounded evidence context
系统 SHALL 在后续业务处理调用中使用任务业务说明、冻结 Skill 修订和此前证据 manifest 构造上下文。系统 MUST 按配置化输入预算对证据进行确定性切片、引用压缩和分块汇总，且不得用模型输出 token 上限代替输入预算校验。

#### Scenario: Historical evidence fits the budget
- **WHEN** 经字段策略处理的历史证据引用和必要切片未超过当前模型连接的安全输入预算
- **THEN** 系统将全部 evidence manifest 条目和允许的证据切片纳入当轮上下文

#### Scenario: Historical evidence exceeds one prompt
- **WHEN** 历史证据超过单次安全输入预算
- **THEN** 系统按稳定顺序分块分析并以带 evidence 引用和 digest 的摘要继续，且不静默丢弃冲突或 unknown

#### Scenario: All blocks cannot fit safely
- **WHEN** 分块数量或最终综合仍超过配置上限
- **THEN** 系统安全拒绝本次模型调用并提示缩小范围，且保留已创建 attempt 的失败阶段

## MODIFIED Requirements

### Requirement: Model calls fail safely
模型调用 SHALL 使用有界超时、单次供应商尝试、输入预算校验和结构化响应校验。查询失败、上下文超限、压缩校验失败、调用失败、超时或输出非法时，系统 MUST 将当前 attempt 标记为失败，MUST NOT 保存成功草稿、生成决策包或回退成未标识的伪模型结果；已成功采集的证据 MUST 以待分析状态保留。

#### Scenario: Provider fails
- **WHEN** 模型服务超时、限流或返回非法结构
- **THEN** API 返回安全的业务错误并记录失败 attempt，不包含密钥、原始提示词、推理内容或供应商响应正文

#### Scenario: Evidence was collected before provider failure
- **WHEN** 只读查询成功但供应商调用随后失败
- **THEN** 系统保留 `COLLECTED_UNANALYZED` 证据，不保存成功草稿且不把证据视为已完成模型分析

#### Scenario: Context validation fails
- **WHEN** 压缩摘要引用不存在、digest 不匹配、unknown 缺失或输入预算超限
- **THEN** 系统在调用供应商前拒绝请求并记录明确失败阶段
