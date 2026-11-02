# Spec Delta

## MODIFIED Requirements

### Requirement: Continue and reselection signals

初稿模型 SHALL 输出 `needMoreEvidence` 和 `needTableReselection` 两个布尔字段。响应 SHALL 返回 `evidenceRound`、`needMoreEvidence`、`needTableReselection`。`needTableReselection=true` 时系统 MUST 失效候选/选表缓存并重建。继续分析仅通过聊天输入触发，MUST NOT 新增继续按钮。

#### Scenario: More evidence needed
- **WHEN** 初稿模型返回 needMoreEvidence=true
- **THEN** 响应带轮次和 true，前端在聊天框输入框 placeholder 显示继续提示

#### Scenario: Reselect tables
- **WHEN** needTableReselection=true
- **THEN** 下一次动作重建候选表并重新选表

#### Scenario: Chat continues analysis
- **WHEN** 用户发送修改意见
- **THEN** 系统执行一轮新查询计划和初稿修订，不并发执行
