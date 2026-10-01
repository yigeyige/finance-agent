# Test Business Pilot Preflight

## Purpose

定义仅在 local/test 使用的测试业务试点预检、非确认状态处理和人工复核材料，确保静态准备不会被误用为真实源、生产事实或运行时试点证据。

## Requirements

### Requirement: Test business pilot preflight is fixed and read-only
系统 SHALL 仅在 local/test 环境基于已登记的多类型测试参考样本生成试点预检摘要。预检 MUST 不访问网络、数据库、真实业务源或生产配置，也 MUST 不产生任何源写入。

#### Scenario: Registered reference samples are preflighted
- **WHEN** 测试业务试点预检读取固定参考样本目录
- **THEN** 返回样本版本、总覆盖数、未知数、非当前问题项和预期人工确认次数

#### Scenario: Production profile attempts to use preflight
- **WHEN** prod profile 尝试装配测试业务试点预检服务
- **THEN** 系统不提供该服务，且不以真实源、默认数据或生产配置替代

### Requirement: Pilot boundaries preserve non-confirmation states
系统 SHALL 将 `FUTURE`、`UNKNOWN` 和 `NO_MATCH` 记录为非当前确认的试点问题项。预检 MUST 不将这些状态推断为当前事实或实际业务影响。

#### Scenario: Boundary sample is included in preflight
- **WHEN** 预检包含未来、未知或无匹配的参考样本
- **THEN** 该样本计入覆盖和问题清单，并要求人工复核而不是生成当前确认结论

### Requirement: Runtime pilot review remains human-owned
系统 SHALL 交付测试业务试点任务包、数据边界报告与人工复核记录模板。静态预检或单元测试 MUST NOT 被标记为运行时试点、真实源授权或生产发布完成。

#### Scenario: Pilot package is reviewed before runtime execution
- **WHEN** 项目负责人审阅试点任务包
- **THEN** 包中列明人工发起、逐步确认、调查、复核、发布、交接和所需运行时证据

### Requirement: Pilot preflight is a metrics baseline input
系统 SHALL 允许测试业务量测基线读取试点预检的覆盖、未知和预期人工确认计数。该输入 MUST 保持固定样本版本与 local/test 边界，且不得替代运行时采集。

#### Scenario: Metrics baseline consumes preflight summary
- **WHEN** 系统生成测试业务样本指标
- **THEN** 指标与预检摘要中的覆盖、未知和人工确认计数一致
