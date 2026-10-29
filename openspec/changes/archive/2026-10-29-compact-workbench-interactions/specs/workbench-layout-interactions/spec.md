## ADDED Requirements

### Requirement: Compact right workspace
系统 SHALL 对全部右侧功能页使用适度紧凑的标题、操作区、卡片与表格间距，保留可读字号和中文标签，保持左侧菜单结构、样式和交互不变。

#### Scenario: Switch between business pages
- **WHEN** 用户切换列表、配置、向导、详情、证据、决策包和数据探索页面
- **THEN** 右侧布局保持一致密度，宽屏列表利用可用宽度，窄屏操作可换行且表格可横向滚动

### Requirement: Remember table column widths
系统 SHALL 支持全部业务表格拖动列宽，并按表格及字段结构保存宽度，仅持久化列标识与宽度；不提供恢复默认列宽、列显隐或列排序设置。长文本 SHALL 省略显示并可查看完整内容。

#### Scenario: Reopen a resized table
- **WHEN** 用户拖动列宽后刷新或重新打开相同表格
- **THEN** 同一字段结构恢复保存宽度，其他表格不受影响

#### Scenario: Storage unavailable or invalid
- **WHEN** 浏览器存储不可用、宽度数据损坏或字段结构发生变化
- **THEN** 表格仍正常展示，不将失效宽度应用于其他字段，不持久化业务行数据

### Requirement: Manipulate business form dialogs
系统 SHALL 为三个业务新增/编辑表单弹窗提供标题栏移动、宽高调整、最大化及还原，尺寸与位置限制于视口，内容区域滚动且页脚操作可见；关闭后再次打开 SHALL 恢复默认尺寸位置。

#### Scenario: Resize and maximize a long form
- **WHEN** 用户缩小、移动或最大化表单弹窗
- **THEN** 弹窗保持在视口内，保存和取消入口可见，最大化可还原，缩放柄与最大化按钮可用键盘操作

#### Scenario: Close or leave the page during dragging
- **WHEN** 用户关闭弹窗或离开页面
- **THEN** 释放拖动监听并清理临时几何状态，再次打开从默认布局开始
