## Why

业务场景 Skill 新增/编辑弹窗初始居中，但在提示词输入框输入内容后自动下移 15vh。原因是 Element UI 重渲染时重新写回默认 `margin-top: 15vh`，与弹窗固定定位叠加，破坏 `v-workbench-dialog` 的位置控制。

## What Changes

- 弹窗打开时由指令显式水平垂直居中，不再依赖 Element UI 的 `margin-top` 默认定位。
- 用 CSS `margin-top: 0 !important` 锁定弹窗 margin-top，阻止 Element UI 重渲染时再次写回 `15vh`。
- 增加打开居中的回归测试，验证输入不位移。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `workbench-layout-interactions`: 业务表单弹窗的打开定位与输入稳定性。

## Impact

仅修改前端 `workbench-dialog.ts` 指令、`workbench-compact.css` 与 `workbench-dialog.test.cjs`；不改变后端接口、数据契约与权限。验证方式为前端测试、类型检查与构建。
