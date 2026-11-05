## ADDED Requirements

### Requirement: Weekly 30-day runtime retention

系统 SHALL 默认在 `Asia/Shanghai` 每周日 02:00 执行一次清理；平台管理员可修改每天、每周或每月频率和对应执行时间，修改后立即重排下一次执行且重启后保持设置。系统以本次运行固定时刻减去 30×24 小时作为截止时间。系统 MUST 按 `design.md` 中逐表清单清理到期业务和运行记录，优先使用表的 `updated_at`，无此列时使用 `created_at` 或列明的业务时间；清理不因任务、审批、发布或 outbox 投递状态而豁免。未列入清单的表 MUST NOT 被自动清理。

#### Scenario: Old records are removed regardless of status

- **WHEN** 清理运行时一条任务、已发布修订或待投递 outbox 的判定时间早于截止时间
- **THEN** 系统按表级规则删除该记录及为满足外键必须连带的引用记录

#### Scenario: New records remain

- **WHEN** 一条记录的判定时间不早于截止时间且没有引用到期父记录
- **THEN** 本次清理保留该记录

#### Scenario: Configuration assets remain

- **WHEN** 账号、授权、运行版本、模型/数据源配置、提示词、场景技能及其附件、知识资产或数据库实例结构目录超过 30 天未更新
- **THEN** 系统仍保留这些配置性表记录

### Requirement: Dependency-safe bounded deletion

系统 SHALL 按显式外键依赖顺序，以有界批次删除到期记录。到期父记录被未到期子记录引用时，系统 SHALL 连带删除该子记录并分别统计；单批失败 MUST 回滚该批、记录错误并允许下次调度重试。系统 MUST 处理任务当前修订/步骤的反向引用和已发布修订自引用。

#### Scenario: Old task has newer artifact

- **WHEN** 到期任务被较新的产物批次或附件引用
- **THEN** 系统先删除关联产物或附件，再删除任务，清理日志分别记录数量

#### Scenario: One batch fails

- **WHEN** 某批因数据库错误无法完成
- **THEN** 该批无部分删除，错误出现在清理日志中，后续运行仍可重试

### Requirement: Owned files and logs expire safely

系统 SHALL 在 `design.md` 列明且应用有所有权的 Workspace、临时对象和日志目录内清理到期文件，即使目前目录未被业务写入也 SHALL 处理其中符合条件的文件；MUST 校验真实路径不越界，MUST 跳过正在写入的文件。Java 应用 SHALL 保持控制台输出，并将可供 Agent 直接读取的应用日志及清理日志写入按日期和大小轮转的本地文件，保留 30 天；本地启动脚本日志 MUST 避免持续运行时单文件无限增长。

#### Scenario: Old file is removed

- **WHEN** 受控目录中一个可验证所有权的非活动文件最后修改时间早于截止时间
- **THEN** 系统删除该文件并记录类别和数量

#### Scenario: Active or out-of-root file is protected

- **WHEN** 文件正在写入、经符号链接越界或不属于已配置目录布局
- **THEN** 系统不删除该文件

#### Scenario: Agent can inspect current debug output

- **WHEN** 开发环境产生新的后端调试日志
- **THEN** Agent 可直接读取当前本地日志文件分析，且旧文件按保留期轮转清理

### Requirement: Administrator can manage scheduled cleanup

系统 SHALL 为平台管理员提供调度任务页面，左侧列出任务，右侧展示所选任务最新一次运行的时间、结果、各类别删除数量和脱敏错误，并允许修改每天、每周或每月频率与执行时间。调度设置 MUST 持久化，修改后立即生效；日志 API MUST 限制读取数量和字节数，MUST NOT 接受任意文件路径。

#### Scenario: Administrator reviews a run

- **WHEN** 具有 `PLATFORM_ADMIN` 与 `CONFIG_MANAGE` 的用户打开调度任务页
- **THEN** 页面左侧显示业务数据清理任务，右侧同一行显示 Java 类名与执行时间，下方显示最新一次运行结果及失败原因

#### Scenario: Administrator changes schedule

- **WHEN** 平台管理员把清理任务改为另一频率及执行时间
- **THEN** 新设置持久保存，下一次执行按 `Asia/Shanghai` 新时间计算，本次已在运行的清理不被中断

#### Scenario: Monthly schedule has an execution date in every month

- **WHEN** 平台管理员选择每月执行
- **THEN** 页面仅提供 1–28 日，后端拒绝范围外的日期

#### Scenario: Unauthorized user is denied

- **WHEN** 非平台管理员访问调度任务设置、清理日志 API 或页面
- **THEN** 系统拒绝访问且不返回调度配置或日志内容
