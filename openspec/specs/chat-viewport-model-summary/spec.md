# 聊天视口与模型摘要

## Purpose

规定业务处理页的统一标题、聊天面板随实际顶部位置填充剩余视口、同排输入发送及紧凑模型信息展示。明确只读运行模型摘要的角色边界与公开字段范围，并将最近一次建议调用的 Token 和耗时与全任务累计消耗严格区分，保持业务流程和模型调用链不变。

## Requirements

### Requirement: Fill the available chat viewport
系统 SHALL 在新建业务处理页显示与任务列表同层级的“业务处理”标题；聊天 SHALL 根据实际顶部位置与视口大小填满可用高度，输入框与“发送”按钮同排，消息区独立滚动，不显示“等待业务说明”。

#### Scenario: Resize viewport or create task
- **WHEN** 视口大小或任务概览导致聊天顶部位置变化
- **THEN** 聊天高度更新到可用区域，清理卸载后的观察与监听，发送区保持可见

### Requirement: Expose a safe runtime model summary
系统 SHALL 为 OPERATOR 和 PLATFORM_ADMIN 提供只含 mode/modelName 的只读模型运行摘要；无可信身份或 VIEWER SHALL 不得读取该摘要，且不得扩大配置维护权限。

#### Scenario: Read runtime summary
- **WHEN** 获准操作员查询当前运行模型
- **THEN** 模拟模式返回明确的模拟模型，真实模式返回已启用模型名或 null，不返回地址、密钥或配置明细

### Requirement: Show scoped model usage metadata
聊天顶部 SHALL 紧凑显示当前模型与最近建议调用 Token/耗时；当前模型来自运行摘要，最近调用取自成功响应 metadata，不将其标记为整个任务总消耗。未调用时 SHALL 不伪造数值，重置草稿时 SHALL 清空旧调用摘要。

#### Scenario: Complete or fail a call
- **WHEN** 模型返回建议或调用失败
- **THEN** 成功时更新最近调用元信息，失败不虚构新消耗；摘要读取失败不阻断原业务流程
