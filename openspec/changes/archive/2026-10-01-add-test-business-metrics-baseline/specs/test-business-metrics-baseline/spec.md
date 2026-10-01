## ADDED Requirements

### Requirement: Test sample metrics are deterministic and bounded
系统 SHALL 基于固定测试业务参考样本和试点预检结果计算事实精确率、召回率、分类准确率、未知率、覆盖和预期人工确认次数。结果 MUST 标识样本版本和 local/test 边界，且 MUST NOT 代表真实业务或生产容量。

#### Scenario: Fixed pilot baseline is calculated
- **WHEN** 系统计算固定测试业务样本的量测基线
- **THEN** 返回可复核的样本指标和人工确认计数，且未知、未来、无匹配状态不被计作当前确认事实

### Requirement: Runtime metrics and thresholds are not prefilled
系统 SHALL 将 API/任务时延、数据库规模、并发、Token、成本和签署阈值标记为待用户采集。系统 MUST NOT 生成虚构运行时数值、通过结论或容量调整结论。

#### Scenario: Runtime measurement template is prepared
- **WHEN** P1 量测基线被交付
- **THEN** 小、中、大三档均列出待采集字段和 `PENDING_USER` 状态，不含预填数值或阈值
