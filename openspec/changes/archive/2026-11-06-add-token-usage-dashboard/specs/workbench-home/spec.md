## ADDED Requirements

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
