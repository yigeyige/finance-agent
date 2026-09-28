## MODIFIED Requirements

### Requirement: Stable paginated enumeration

系统 SHALL 在有界页数内按连续页号枚举只读来源，并记录每页路径、页数、预期总量、实际总量、水位和结果集摘要。

#### Scenario: Complete stable traversal
- **WHEN** 所有页按顺序返回、预期总量与实际总量一致、水位和路径均稳定
- **THEN** 系统返回 `COMPLETE` 覆盖度并生成包含稳定结果摘要的覆盖记录

#### Scenario: Pagination loop guard
- **WHEN** 来源重复返回相同 continuation token 或页号不再前进
- **THEN** 系统停止枚举并将覆盖度标记为 `UNKNOWN`，不得继续请求无限页

### Requirement: Coverage degradation on inconsistent source

系统 SHALL 对缺页、总量变化、路径失败和水位变化进行显式记录，并禁止将不一致结果标记为完整覆盖。

#### Scenario: Partial traversal
- **WHEN** 来源在中途失败、页被遗漏或实际总量小于预期总量
- **THEN** 系统返回 `PARTIAL`，并在失败或未覆盖路径字段中保留原因

#### Scenario: Unknown consistency
- **WHEN** 来源水位变化、预期总量变化或一致性无法确认
- **THEN** 系统返回 `UNKNOWN`，并保留对应诊断原因

### Requirement: Coverage evidence record

系统 SHALL 复用现有 `CoverageRecord` 领域模型生成可持久化的覆盖度证据，包含查询版本、条件摘要、枚举方法、计数、路径、失败、水位、结果摘要和完整性。

#### Scenario: Build evidence record
- **WHEN** 调用方提供任务、步骤、范围版本、查询元数据、遍历结果、检查时间和审计身份
- **THEN** 系统生成字段完整且可通过领域校验的 `CoverageRecord`
