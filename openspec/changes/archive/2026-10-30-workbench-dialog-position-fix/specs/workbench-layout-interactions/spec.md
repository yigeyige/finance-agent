## MODIFIED Requirements

### Requirement: Manipulate business form dialogs
系统 SHALL 为三个业务新增/编辑表单弹窗提供标题栏移动、宽高调整、最大化及还原，尺寸与位置限制于视口，内容区域滚动且页脚操作可见；打开时 SHALL 水平垂直居中，输入内容触发重渲染时 SHALL 保持位置不因框架默认 margin 重新写入而位移；关闭后再次打开 SHALL 恢复默认居中位置。

#### Scenario: Resize and maximize a long form
- **WHEN** 用户缩小、移动或最大化表单弹窗
- **THEN** 弹窗保持在视口内，保存和取消入口可见，最大化可还原，缩放柄与最大化按钮可用键盘操作

#### Scenario: Type into a long prompt field
- **WHEN** 用户在弹窗的提示词等多行输入框中输入内容
- **THEN** 弹窗保持居中不向下移动，仅正文区域滚动

#### Scenario: Close or leave the page during dragging
- **WHEN** 用户关闭弹窗或离开页面
- **THEN** 释放拖动监听并清理临时几何状态，再次打开从默认居中位置开始
