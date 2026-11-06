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

### Requirement: Home page shows 30-day token usage

首页 SHALL 在右侧栏展示 token 用量卡片：普通用户展示本人用量，平台管理员展示全平台用量；卡片 SHALL 包含累计总量、最近 30 天折线与统计口径说明。数据 SHALL 在进入首页时请求一次并在 5 分钟内复用缓存；请求失败或超时 MUST 显示“用量暂不可用”，且 MUST NOT 影响 hero、业务处理快捷卡与三步指南的展示。

#### Scenario: Business user opens home page

- **WHEN** 普通已登录用户进入首页
- **THEN** 右侧用量卡展示本人 30 天总量与折线，并标注“我的用量”

#### Scenario: Administrator opens home page

- **WHEN** 平台管理员进入首页
- **THEN** 右侧用量卡展示全平台 30 天总量与折线，并标注“全平台用量”

#### Scenario: Usage data is unavailable

- **WHEN** 用量接口失败、超时或返回无法解析的数据
- **THEN** 卡片展示“用量暂不可用”，首页其余内容正常可用

#### Scenario: Re-enter home page within cache window

- **WHEN** 用户在 5 分钟内再次回到首页
- **THEN** 前端复用上次用量数据，不重复请求接口

#### Scenario: Cache is scoped to the current identity

- **WHEN** 同一浏览器切换账号后进入首页
- **THEN** 前端不使用上一个账号的缓存用量，按新身份的 scope 与用户标识重新请求

#### Scenario: Viewport changes while card is visible

- **WHEN** 浏览器窗口尺寸变化
- **THEN** 折线图随卡片宽度重新布局，不出现裁切或错位
