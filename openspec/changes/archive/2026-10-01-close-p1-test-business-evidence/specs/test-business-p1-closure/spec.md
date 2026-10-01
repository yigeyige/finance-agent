## ADDED Requirements

### Requirement: P1 test business closure evidence is bounded and traceable
系统 SHALL 为 P1 测试业务源形成可审阅的闭环证据包，逐项列出固定测试范围内已核对的目录、字段策略、样本、交叉印证、预检、量测与自动化回归。每项结论 MUST 指向仓库内工件，并标识为已关闭、待人工验证或 P2 阻断之一。

#### Scenario: Static P1 closure is reviewed
- **WHEN** 项目负责人审阅 P1 测试业务闭环证据包
- **THEN** 能够追溯每个已关闭结论的工件和验证方式，且不会将未执行活动标记为通过

### Requirement: P2 inputs remain non-executable and blocked
系统 SHALL 将真实源授权、真实字段权限、正式统一认证、运行时原始量测、容量、成本、保留、正式例外和角色签署列入 P2 输入清单。P1 闭环 MUST NOT 将这些输入转化为 P2 实施、真实源访问或生产放行结论。

#### Scenario: P1 closure is completed before P2 entry
- **WHEN** P1 测试业务闭环被标记完成
- **THEN** P2 输入清单仍保留未满足项、责任角色和所需证据，且没有真实端点、凭证或批准结论

### Requirement: Role sign-off templates do not fabricate approval
系统 SHALL 为技术、功能和数据源角色提供签署模板。模板 MUST 保持签署人、日期、适用范围、证据引用和结论字段为空或 `PENDING_USER`，直到相应人工流程实际完成。

#### Scenario: Unsigned template is delivered
- **WHEN** P1 闭环交付签署模板
- **THEN** 模板可供后续人工填写，但不包含预填签名、姓名、通过结论或真实源信息
