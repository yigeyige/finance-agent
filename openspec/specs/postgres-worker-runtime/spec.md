# postgres-worker-runtime

## Purpose

定义 PostgreSQL Worker 的步骤领取、租约生命周期、有限资源调度和优雅停止边界，确保多实例执行不会重复占用任务。

## Requirements

### Requirement: Worker claims only executable approved steps

系统 SHALL 使用稳定排序的 `FOR UPDATE SKIP LOCKED` 查询领取 `WAITING_STEP_APPROVAL` 任务下状态为 `APPROVED` 的步骤，并在同一事务内创建活动租约、将步骤置为 `RUNNING`、将任务推进为 `RUNNING_STEP`。等待人工或未获批步骤不得创建租约。

#### Scenario: Approved step is claimed atomically
- **WHEN** Worker 领取一个已获批步骤且不存在未释放租约
- **THEN** 任务行和步骤行在领取事务中锁定，租约、步骤状态和任务状态一起提交

#### Scenario: Locked step is skipped
- **WHEN** 另一个事务已经锁定候选步骤
- **THEN** `SKIP LOCKED` 查询跳过该步骤，当前 Worker 不等待该锁，也不创建租约

#### Scenario: Waiting human step is not claimed
- **WHEN** 任务处于等待人工确认或步骤不是 `APPROVED`
- **THEN** 领取服务返回无可执行步骤，数据库中不产生活动租约

### Requirement: Lease lifecycle is owner-bound and time-bounded

系统 SHALL 提供租约获取、按 owner 心跳、按 owner 释放和过期接管。心跳与释放必须匹配 lease id、owner instance 和未释放条件；只有 `expiresAt` 严格早于观察时刻的租约可以被接管，未过期或时间相等时不得接管。

#### Scenario: Heartbeat renews the same owner lease
- **WHEN** 当前 owner 在租约过期前提交有效心跳
- **THEN** 仅该租约的 heartbeat 和 expires 时间更新，返回成功

#### Scenario: Stale owner cannot renew or release
- **WHEN** 旧 Worker 在租约已被接管或 owner 不匹配时提交心跳或释放
- **THEN** 更新行数为零，服务返回租约丢失结果，不改变当前活动租约

#### Scenario: Expired lease is taken over once
- **WHEN** 任务行被锁定且活动租约严格早于观察时刻
- **THEN** 旧租约先被释放，新 Worker 获得唯一活动租约；并发接管者最多一个成功

#### Scenario: Clock skew boundary is safe
- **WHEN** 活动租约 expiresAt 等于或晚于观察时刻
- **THEN** 接管被拒绝，避免因边界或时钟偏差造成双执行

### Requirement: Worker lifecycle is bounded and gracefully stoppable

系统 SHALL 提供可配置的 Worker 调度器、独立有界线程池和独立有界数据库连接池。停止流程必须先停止领取，再等待已领取处理器在安全点结束；等待人工状态不得占用 Worker 线程。Worker 默认关闭，配置启用时才启动。

#### Scenario: Worker starts with validated bounds
- **WHEN** Worker 配置启用且线程数、队列容量、轮询间隔、租约时长、心跳间隔和关闭宽限期通过校验
- **THEN** 调度器使用独立命名线程池和 Worker 连接池启动，并按轮询间隔尝试领取

#### Scenario: Invalid heartbeat configuration is rejected
- **WHEN** 心跳间隔大于或等于租约时长，或任一资源参数非正/超过上限
- **THEN** 配置绑定失败，Worker 不启动

#### Scenario: Stop prevents new claims
- **WHEN** Spring 关闭上下文或调用 Worker stop
- **THEN** 调度器不再提交新的领取任务，已领取处理器获得关闭宽限期完成安全点
