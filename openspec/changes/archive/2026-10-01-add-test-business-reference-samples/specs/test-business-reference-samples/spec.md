## ADDED Requirements

### Requirement: Versioned multi-type test reference samples
系统 SHALL 在 local/test 环境提供版本化、固定且完全虚构的参考样本，覆盖人员、岗位、角色、流程节点及组织/权限范围。每个样本 MUST 使用测试标识，且不得包含真实人员、金额、单据、凭证、生产配置或连接信息。

#### Scenario: Catalog is read in local or test profile
- **WHEN** local 或 test profile 读取多类型参考样本目录
- **THEN** 系统返回版本、稳定样本标识和完整的测试对象关联，且样本不依赖外部系统

#### Scenario: Production profile attempts to use catalog
- **WHEN** prod profile 尝试装配或读取多类型参考样本目录
- **THEN** 系统不提供该目录，且不以真实源或默认数据替代

### Requirement: Golden facts preserve non-confirmation boundaries
系统 SHALL 为当前、未来、未知和无匹配测试情形声明可机器核对的黄金事实。`UNKNOWN` 与 `NO_MATCH` MUST 保持为非确认结果，`FUTURE` MUST 不被视为当前生效事实。

#### Scenario: Boundary samples are evaluated
- **WHEN** 测试按样本标识读取当前、未来、未知或无匹配的黄金事实
- **THEN** 返回的预期状态与冻结样本一致，且未知、无匹配和未来状态不产生当前确认结论

### Requirement: Test reference sample scope is reviewable
系统 SHALL 交付样本矩阵、黄金事实与适用范围确认记录。确认记录 MUST 标明当前会话项目负责人、日期和仅限 local/test 的边界，且 MUST NOT 构成真实源、生产认证、真实字段授权或生产发布批准。

#### Scenario: Reference sample package is reviewed
- **WHEN** P1 多类型参考样本被审阅
- **THEN** 审阅者能够核对对象类型、状态边界、样本版本、适用范围与测试范围确认结论
