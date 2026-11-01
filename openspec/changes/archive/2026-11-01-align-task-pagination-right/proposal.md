## Why

用户截图显示任务列表左下角留有总数和每页条数控件，而翻页按钮被推到右侧。分页器被拆成两组，不符合用户期望的右下角集中布局，也让空列表时的位置尤其突兀。

## What Changes

- 将任务列表的总数、每页条数和翻页控件集中显示在表格右下角。
- 保留现有服务端分页与页面大小切换逻辑。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `task-list-audit-query`：明确分页器整体右对齐的展示要求。

## Impact

仅影响任务列表分页布局与相关规格，不改变接口、数据或权限。
