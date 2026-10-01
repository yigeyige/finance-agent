## ADDED Requirements

### Requirement: First test business adapter acceptance record
系统 SHALL 为首个测试业务只读适配器交付可审阅的 SourceCatalog 版本、查询模板、字段投影、fixture 版本和 TCK 结果。该记录 MUST 明确其仅适用于 local/test，且不得包含真实人员、金额、单据、凭证、生产配置或连接信息。

#### Scenario: Adapter technical acceptance is reviewed
- **WHEN** 首个测试业务只读适配器完成技术验收
- **THEN** 验收记录能够核对目录、字段投影、只读限制、拒绝路径、契约漂移和故障语义的测试结果
