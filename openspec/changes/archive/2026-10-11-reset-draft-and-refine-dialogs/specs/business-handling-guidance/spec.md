## ADDED Requirements

### Requirement: Business scenario Skill dialog consistency
系统 SHALL 为业务场景 Skill 的新增和编辑弹窗提供与工作台一致的暖白圆角容器、清晰标题区、可读表单间距和底部操作区；窄屏下表单和按钮 MUST 保持可操作。字段校验、删除确认、保存行为和开始处理入口 MUST 保持不变。

#### Scenario: Administrator opens a business scenario Skill dialog
- **WHEN** OPERATOR 打开新增或编辑业务场景 Skill 弹窗
- **THEN** 页面以统一工作台弹窗样式展示已有字段和保存、取消操作，且不改变当前业务数据
