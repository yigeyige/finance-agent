## ADDED Requirements

### Requirement: Test source cross-validation evidence and scope confirmation
系统 SHALL 交付第二测试来源的目录版本、来源权威顺序、一致/冲突样本、TCK 结果和测试业务范围确认记录。确认记录 MUST 标明当前会话项目负责人、确认日期和仅限 local/test 的边界，且 MUST NOT 构成真实源、生产认证、字段授权或生产放行批准。

#### Scenario: Technical cross-validation package is reviewed
- **WHEN** 测试交叉印证技术包被审阅并完成测试业务范围确认
- **THEN** 审阅者能够核对两个测试来源、对象范围、冲突保留、失败关闭和确认边界
