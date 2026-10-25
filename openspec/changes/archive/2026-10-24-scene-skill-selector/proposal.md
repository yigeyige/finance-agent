# Proposal

## Why

新建业务处理界面（`/#/`）第一步与第二步的「业务场景」下拉框是硬编码的 4 个选项（业务调整/人员变更/审批异常/配置调整），不展示业务场景 Skill 列表。用户直接打开工作台新建任务时无法选定 Skill，导致任务不关联 Skill、模型缺少业务知识、premiseType 输出「待核验」、证据查询不准。

## What Changes

- 第一步新增「业务场景」下拉框，动态加载业务场景 Skill 列表（`listBusinessSceneSkills`），用户选定后 `createTask` 传 `businessSceneSkillId`。
- 第二步「业务场景」下拉框从硬编码改为展示 Skill 列表（`value=premiseType`，`allow-create` 兼容模型回填的自由文本）。

## Capabilities

### Modified Capabilities

- `business-handling-guidance`: 新建业务处理时可在界面选定业务场景 Skill，任务据此关联 Skill。

## Impact

- 前端：`frontend/src/views/WorkbenchHome.vue`（新增 Skill 列表加载、两步下拉框改为动态展示、createTask 传 skillId）。
- 不改变后端接口契约。
