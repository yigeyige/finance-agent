## Context

`v-workbench-dialog` 指令在打开时读取 Element UI 的默认位置（依赖 `margin-top: 15vh`）并固定为 `position: fixed; margin: 0; top/left/width/height`。Element UI `el-dialog` 的 `:style` 绑定在每次重渲染时重新写入 `margin-top: 15vh`，叠加到固定 `top` 上导致下移。

## Goals / Non-Goals

**Goals:** 弹窗打开居中且输入触发重渲染时位置稳定；移动、缩放、最大化、关闭重置保持不变。

**Non-Goals:** 不改变后端、数据契约、权限，不新增其他弹窗能力。

## Decisions

- 指令打开时改为 `centerOnOpen` 显式计算水平垂直居中位置，不再读取 Element UI 的 margin-top 定位。
- 在 `.workbench-dialog.workbench-dialog-interactive` 增加 `margin-top: 0 !important`，覆盖 Element UI 重渲染写回的 inline `margin-top: 15vh`。
- 复用 `apply`/`constrainDialog` 做视口约束，移动、缩放、最大化逻辑不变。
- 新增测试锁定居中坐标与 `margin: 0`。

## Risks / Trade-offs

`!important` 仅作用于指令挂载的 `.workbench-dialog-interactive` 弹窗，不影响其他 Element UI 弹窗。首次打开由指令居中，替代原先依赖 15vh 的近似居中。

## Migration Plan

纯前端样式与指令调整，无迁移。实施后运行测试、类型检查与构建，交用户手动验收。

## Open Questions

无。
