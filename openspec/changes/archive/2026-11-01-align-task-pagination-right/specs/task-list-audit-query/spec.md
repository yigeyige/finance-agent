## ADDED Requirements

### Requirement: Task list pagination controls align as one group

任务列表 SHALL 将总数、每页条数与翻页控件作为一组放在表格右下角；空列表时也 SHALL 保持相同的对齐方式。窄屏下控件 MUST 保持可访问。

#### Scenario: Empty task list

- **WHEN** 筛选结果为零条
- **THEN** 总数和每页条数控件位于表格右下角，不单独留在左侧

#### Scenario: Populated task list

- **WHEN** 任务列表有多页记录
- **THEN** 总数、每页条数和翻页按钮在右下角连续排列，分页请求行为保持不变

#### Scenario: Narrow viewport

- **WHEN** 视口不足以单行容纳分页控件
- **THEN** 控件允许换行且仍可操作
