# Tasks

## 1. 决策包步骤视口滚动

- [x] 1.1 将 `WorkbenchHome.vue` 顶层 `--viewport` 条件由 `activeStep < 2` 改为 `activeStep !== 0`；验证决策包步骤左侧卡片内部滚动、右侧聊天框保持可见

## 2. 修订标签位置

- [x] 2.1 用 `packet-title-row` 包裹“业务决策包”标题与“修订”标签，移出 `collaboration-actions`；验证修订标签位于标题右侧且垂直居中

## 3. 扁平化“业务决策包”层

- [x] 3.1 用 `.guidance-result-panel.packet-result-panel` 覆盖 padding/border/background/margin-top，去掉独立卡片外观；验证子卡片直接平铺在外壳卡片内、流程文件卡片仍保留卡片外观

## 4. 前端验证

- [x] 4.1 运行前端类型检查与生产构建，验证无报错

## Workflow follow-up

- 实现与验证通过后归档该 change 并同步 delta spec 到主规格。
