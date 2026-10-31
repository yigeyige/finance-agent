## ADDED Requirements

### Requirement: Instance-scoped configuration schema snapshot

系统 SHALL 为每个 EMS 数据库实例独立保存最近一次成功同步的配置结构目录及同步时间。目录 MUST 仅包含 `basedata`、`activiti_ems` 的库名、表名、表注释、字段名、字段类型和字段注释，MUST NOT 包含业务行值、连接凭据或 `claim` 结构。系统 MUST 区分从未成功同步与已成功同步但目录为空；实例连接配置发生编辑时，旧目录 SHALL 保留至管理员再次成功同步。

#### Scenario: One instance has a successful snapshot

- **WHEN** 管理员为一个实例成功同步结构目录
- **THEN** 系统只替换该实例的完整目录并记录成功同步时间，其他实例目录不变

#### Scenario: Empty successful snapshot

- **WHEN** 同步成功但全部表均被过滤或为空
- **THEN** 系统仍记录该实例已成功同步和同步时间，目录为空，不能将其视为从未同步

#### Scenario: Connection settings change

- **WHEN** 管理员修改已有实例的 JDBC 地址、账号或其他连接属性
- **THEN** 已成功同步的目录仍可使用，直到管理员主动再次成功同步；页面显示上次同步时间

### Requirement: Administrator manually synchronizes read-only schema metadata

系统 SHALL 在现有数据库实例管理入口向 `PLATFORM_ADMIN` 提供手动“同步表结构”操作。同步 MUST 只读访问目标实例的允许库元数据，并按约定排除日志、历史、备份、日期后缀、临时/测试/复制、事务与 MQ 流水、`act_hi_*`、`act_ru_*` 及同步时没有记录的表。空表判断 MUST 使用实际只读存在性检查，MUST NOT 仅依据估计行数。同步失败 MUST 保留上一份成功目录，并返回不含连接串、凭据、原始 SQL 或驱动详情的安全错误。

#### Scenario: Administrator synchronizes an instance

- **WHEN** `PLATFORM_ADMIN` 对已配置且可访问的实例点击“同步表结构”
- **THEN** 系统从 `basedata`、`activiti_ems` 收集并过滤表字段元数据，整份更新该实例目录，页面显示新的成功同步时间

#### Scenario: Empty table is excluded

- **WHEN** 某候选表在本次同步的实际只读存在性检查中没有记录
- **THEN** 该表及其字段不进入本次目录；日后管理员再次同步且该表已有记录时可纳入

#### Scenario: Sync fails midway

- **WHEN** 同步过程中连接、元数据读取或存在性检查失败
- **THEN** 本次同步失败且上一份成功目录及其同步时间保持不变，不保存半成品

#### Scenario: Unauthorized user requests synchronization

- **WHEN** 非 `PLATFORM_ADMIN` 身份请求同步接口
- **THEN** 系统拒绝请求，不读取 EMS 元数据，也不修改目录
