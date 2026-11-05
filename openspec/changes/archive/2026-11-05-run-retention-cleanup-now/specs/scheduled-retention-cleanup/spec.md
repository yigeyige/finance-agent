## ADDED Requirements

### Requirement: Administrator can run the fixed cleanup task once

系统 SHALL 允许平台管理员从调度任务页确认后手动提交一次固定业务数据清理任务。手动运行 MUST 复用定时运行的 30 天清理范围、执行互斥和日志，MUST NOT 保存未提交的调度时间草稿或改变下一次定时触发。任务在后台执行，重复手动提交 MUST 被拒绝，非平台管理员 MUST 被拒绝。

#### Scenario: Administrator runs cleanup now

- **WHEN** 平台管理员点击执行时间“保存”右侧的“立即执行一次”并确认
- **THEN** 系统后台提交一次业务数据清理，并在完成后于最新调度日志展示结果；既有调度设置保持不变

#### Scenario: Duplicate submission is rejected

- **WHEN** 一次手动运行仍在排队或执行时再次提交
- **THEN** 系统拒绝第二次提交，避免重复排队

#### Scenario: Unauthorized or cancelled request does not run

- **WHEN** 非平台管理员调用执行接口，或管理员取消页面确认
- **THEN** 系统不提交清理任务
