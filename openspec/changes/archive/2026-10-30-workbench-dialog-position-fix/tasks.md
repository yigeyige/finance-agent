## 1. 弹窗定位稳定性

- [x] 1.1 workbench-dialog.ts：打开时显式居中 centerOnOpen，替代依赖 margin-top 的 fitViewport
- [x] 1.2 workbench-compact.css：锁定弹窗 margin-top 为 0，阻止重渲染写回 15vh
- [x] 1.3 workbench-dialog.test.cjs：新增打开居中与 margin 归零回归测试

## 2. 验证

- [x] 2.1 运行前端测试、类型检查与构建，浏览器验证输入前后位置不变
