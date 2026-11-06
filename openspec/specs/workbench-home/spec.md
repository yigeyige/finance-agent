# workbench-home Specification

## Purpose
规定登录后首页 `/home` 的展示内容与快捷入口：向所有已登录用户介绍 Agent 定位与三步使用流程，并提供进入业务处理页 `/handling` 的快捷入口，不读取任何业务数据。

## Requirements

### Requirement: Home page introduces the agent and its workflow

首页 `/home` SHALL 向所有已登录用户展示 Agent 定位说明与三步使用流程（业务说明、模型协作、决策包），且 MUST NOT 请求任务、证据或配置数据，不依赖 `TASK_READ`、`CONFIG_MANAGE` 等业务权限。

#### Scenario: Logged-in user opens home page

- **WHEN** 已登录用户进入 `/home`
- **THEN** 页面展示产品主标题、副标题、能力标签和三步使用说明，顶栏不再重复展示产品大标题，右上角用户菜单保持可见

#### Scenario: Home page loads without task permission

- **WHEN** 仅具备登录态、没有 `TASK_READ` 权限的用户进入 `/home`
- **THEN** 首页正常展示介绍与三步说明，不发起任何业务数据请求

### Requirement: Home page offers a business handling quick entry

首页 SHALL 提供业务处理快捷卡，点击后 MUST 导航到 `/handling` 并携带新的草稿标识，使业务处理页回到空白新任务状态。

#### Scenario: Quick entry starts a new handling

- **WHEN** 用户点击首页业务处理快捷卡的“进入业务处理”
- **THEN** 前端导航到 `/handling?draft=<时间戳>`，业务处理页重置上一次未完成的表单与附件，既有后端任务不受影响
