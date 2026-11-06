## ADDED Requirements

### Requirement: Live model calls are recorded with user attribution

系统 MUST 为每次真实（`LIVE`）模型调用记录一条 token 用量，包含用户标识、任务标识、模型名、调用模式、token 数量与发生时间；归属用户优先取当前请求身份，后台线程无身份时 MUST 回落为任务发起人。`STUB` 模拟调用 MUST NOT 记录。用量写入失败 MUST NOT 改变模型调用结果、MUST NOT 向调用方抛出异常。

覆盖范围 MUST 包含一次业务处理中的全部真实调用：选表、查询规划、业务初稿（含修订多轮）、调查方案、证据分析、决策包分析与流程文件生成。

#### Scenario: Request-thread call is attributed to current identity

- **WHEN** 已登录用户触发一次 LIVE 模型调用
- **THEN** 系统写入一条该用户的用量记录，模型返回结果不受影响

#### Scenario: Background call falls back to task initiator

- **WHEN** 模型调用发生在没有请求身份的线程中
- **THEN** 用量记录归属到该调用所属任务的发起人；无法解析任务时丢弃记录并只记日志

#### Scenario: Stub mode is not counted

- **WHEN** 系统运行在 STUB 模拟模式
- **THEN** 不写入任何用量记录，用量接口返回 0，不把模拟 token 计入统计

#### Scenario: Usage write failure does not break model call

- **WHEN** 用量写入抛出数据库异常
- **THEN** 系统只记录 WARN 日志，模型调用照常返回业务结果

#### Scenario: Failed or zero-token call is not counted

- **WHEN** 模型调用抛出异常、返回空响应，或元数据中 tokens 为 0
- **THEN** 系统不写入用量记录，该次调用不计入总量与趋势

#### Scenario: Cached table selection is not counted

- **WHEN** 选表结果命中任务级缓存，本次请求没有发起真实模型调用
- **THEN** 不写入新的用量记录

### Requirement: Token usage is queryable as a fixed 30-day series

系统 SHALL 提供 `GET /api/v1/usage/tokens`，固定返回最近 30 个自然日（含今天，按 `Asia/Shanghai` 切分）的 token 总量与按天序列，缺失日期 MUST 补 0。普通用户 MUST 只读取本人数据；`PLATFORM_ADMIN` MUST 读取全平台数据。未登录请求 MUST 被拒绝。

#### Scenario: Business user reads own usage

- **WHEN** 普通已登录用户请求用量接口
- **THEN** 返回 `scope = SELF`、本人 30 天总量与按天序列，不包含其他用户的数据

#### Scenario: Administrator reads platform-wide usage

- **WHEN** 平台管理员请求用量接口
- **THEN** 返回 `scope = ALL` 与全平台 30 天总量和按天序列

#### Scenario: No usage in range

- **WHEN** 统计区间内没有任何 LIVE 调用
- **THEN** 返回总量 0 与 30 个日期、token 均为 0 的序列

#### Scenario: Unauthenticated request

- **WHEN** 未登录请求访问用量接口
- **THEN** 系统拒绝该请求
