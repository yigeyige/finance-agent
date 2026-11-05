## Context

当前应用为单体。已用本地 `finance_agent` PostgreSQL 的 `information_schema` 与 Flyway 迁移核对：应用 schema 共 44 张表；31 张为本次业务/运行记录清理候选，13 张为配置或知识资产。现有任务附件原始字节在 `task.task_attachment.content`，生成文件正文在 `task.process_artifact_file.content_text`，并无对应的常规磁盘附件目录。Logback 当前仅写控制台；`scripts/start-local.ps1` 产生按启动时间命名的 stdout/stderr 文件，长时间运行不会自动轮转。

保留期限固定 30×24 小时。默认调度为每周日 02:00，可选每天、每周或每月，时区固定 `Asia/Shanghai`；部署后的首次执行等到下一个调度时刻，不在启动时突发删除。实际清理时间可晚于到期时间一个调度周期。用户自行启动本地环境并手动验收；实施阶段仅做静态检查、编译及自动化验证。

## Goals / Non-Goals

**Goals:**

- 逐表显式清理到期业务/运行记录，覆盖任务、证据、决策包、审批、Harness、审计和 outbox；无状态豁免。
- 列出清理表、判定字段、外键处理、文件目录和排除表，供实施前检查。
- 清理受控 Workspace、临时对象目录和日志中的到期文件；保持本地可读取的调试日志，同时按日期和大小轮转并删除 30 天前的旧日志文件；平台管理员可只读查看清理执行日志。
- PostgreSQL 删除行后的空间可供数据库复用；不要求数据库文件即时缩小。

**Non-Goals:**

- 删除账号、场景技能、知识资产、模型/数据库连接配置、运行版本、提示词模板或 Flyway 元数据。
- 备份、手动执行、页面修改保留期、生产发布或数据库文件强制压缩。
- 遍历未知部署平台的容器运行时日志、宿主机日志或任意外部对象存储。

## Decisions

### 1. 表级清理清单与时间字段

下表每行都是**一张表**，不是按任务主表年龄一刀切。`updated_at` 非空时直接使用；仅无此列时才使用 `created_at`。`task.process_artifact_file` 无时间列，按其所属 `process_artifact_batch.created_at`。业务时间列如 `checked_at`、`published_at` 不覆盖已有 `updated_at`；未来新增无这两个字段的运行表，必须显式指定业务时间后才能纳入。到期条件统一为时间值严格早于本次运行固定的 `now - 30 days`。

| schema | 表 | 判定字段 | 清理内容 / 关联 |
|---|---|---|---|
| task | `investigation_task` | `updated_at` | 任务主记录；删除前处理当前修订/步骤反向 FK 及所有引用者 |
| task | `scope_revision` | `updated_at` | 范围修订 |
| task | `task_premise` | `updated_at` | 前提 |
| task | `investigation_step` | `updated_at` | 调查步骤 |
| task | `step_approval` | `updated_at` | 步骤审批 |
| task | `task_lease` | `updated_at` | 租约，不按租约状态豁免 |
| task | `tool_invocation` | `updated_at` | 工具调用 |
| task | `task_business_scene_skill` | `created_at` | 任务与技能的关联；技能配置本身保留 |
| task | `guidance_draft` | `created_at` | 业务初稿 |
| task | `business_evidence_accum` | `created_at` | 多轮证据累积 |
| task | `business_decision_packet` | `created_at` | 业务决策包，含发布后的关联 |
| task | `process_artifact_batch` | `created_at` | 生成产物批次；现有生成时“最近两批”规则仍适用 |
| task | `process_artifact_file` | 所属批次 `created_at` | 生成文件正文；批次外键已有级联 |
| task | `task_attachment` | `created_at` | 上传附件原始字节及文本投影 |
| evidence | `fact_snapshot` | `updated_at` | 事实快照 |
| evidence | `coverage_record` | `updated_at` | 覆盖记录 |
| evidence | `finding` | `updated_at` | 判断 |
| evidence | `finding_evidence` | `updated_at` | 判断与快照关系 |
| evidence | `computation_witness` | `updated_at` | 计算见证 |
| evidence | `professional_confirmation` | `updated_at` | 专业确认 |
| packet | `packet_draft` | `updated_at` | 决策包草稿 |
| packet | `packet_review` | `updated_at` | 草稿复核 |
| packet | `packet_revision` | `updated_at` | 已发布修订，含 `supersedes_revision_id` 链 |
| packet | `handoff` | `updated_at` | 发布后交接 |
| packet | `scope_recheck` | `updated_at` | 发布前范围重查 |
| harness | `session_state` | `updated_at` | 会话状态 |
| harness | `checkpoint` | `updated_at` | 检查点 |
| harness | `plan_revision` | `updated_at` | 计划修订 |
| harness | `workspace_ref` | `updated_at` | 工作区引用 |
| governance | `audit_event` | `updated_at` | 审计摘要链，含 `task_id IS NULL` 的运行审计；需改变现有禁止删除触发器 |
| governance | `outbox_event` | `updated_at` | 本地事件；待投递和已投递均可到期删除 |

**明确保留的 13 张表：**

| schema | 表 | 原因 |
|---|---|---|
| iam | `agent_user`, `role_binding`, `user_password` | 账号与授权配置 |
| governance | `runtime_bundle`, `model_connection_config`, `ems_database_connection_config`, `prompt_template` | 运行/模型/数据源/提示词配置 |
| task | `business_scene_skill`, `business_scene_skill_attachment` | 场景技能及知识附件配置 |
| knowledge | `embedding_set`, `document`, `chunk`, `ems_instance_schema_snapshot` | 知识资产和数据库实例结构目录配置快照 |

上述 44 表是当前数据库与迁移的交集盘点，其中 31 张清理、13 张保留。Flyway 的 `flyway_schema_history` 属迁移元数据，不纳入业务清理。新增表默认不清理，只有更新本清单、时间字段和依赖图后才进入调度。

### 2. 外键、并发与审计链

采用显式清理计划和有界批次，而不是对 schema 执行泛化 `DELETE WHERE updated_at ...`。每表在事务内选取到期行的 `ctid` 并以 `FOR UPDATE SKIP LOCKED` 锁定后删除；Flyway 将 31 张运行表之间的 `NO ACTION` 外键改为 `ON DELETE CASCADE`，使任务主表的反向引用、修订自引用和较新子记录随到期父行一起删除。配置表外键不改造。绝大多数 FK 原为 `NO ACTION`，仅 `process_artifact_file → process_artifact_batch` 已有 `ON DELETE CASCADE`。父记录到期而引用它的子记录尚未到期时，按已确认的“到期就清理”规则连带删除子记录，并在清理日志分别计数。整批失败回滚该批，记录错误，下一次调度可重试；批次之间可继续，避免单条异常阻断全部清理。

现有 `governance.audit_event` 触发器禁止任意 DELETE。迁移应改为仅专用清理事务可删除到期审计记录；普通写路径仍只追加，不能 UPDATE 或任意 DELETE。按到期任务删除时删除该任务的全部关联事件；若任务未到期而仅旧审计事件到期，较新事件已有的 `prev_event_digest` 值保持不变，不重写摘要或新增验证机制。`task_id IS NULL` 的全局事件按自身时间清理。不要把仅有 `aggregate_id` 文本的 outbox 当作外键；按自身时间清理。清理写事务与正常写入并发时锁定候选键并重新判定时间，避免竞态误删。

### 3. 文件目录清单与边界

| 位置 | 当前来源 / 实际路径 | 清理规则 |
|---|---|---|
| 数据库附件 | `task.task_attachment.content`、`extracted_text` | 由表级清理负责；不是文件目录 |
| 数据库生成产物 | `task.process_artifact_file.content_text` | 由表级清理负责；不是文件目录 |
| 受控 Workspace | `FINANCE_AGENT_WORKSPACE_ROOT`；local 默认 `<backend 工作目录>/.runtime/finance-agent`，test 默认 `<backend 工作目录>/.runtime/finance-agent-test`；Compose 为 `/opt/finance-agent/.runtime/finance-agent`（挂载于 `backend-runtime` volume） | 即使当前未发现业务调用，仍清理超过 30 天的受控工作区文件；只允许 `<root>/<userId>/<taskId>/<sessionId>` 完整身份边界，空目录随后清理，不递归删除整个 root |
| 临时对象存储 | `FINANCE_AGENT_STORAGE_TEMPORARY_ROOT`；local 默认 `<backend 工作目录>/.runtime/finance-agent/objects`，test 默认 `<backend 工作目录>/.runtime/finance-agent-test/objects` | 当前任务附件/产物不写这里，网关目前无业务调用；仍清理可验证所有权且超过 30 天的临时对象。它嵌于默认 Workspace root，必须与工作区扫描分开；prod 默认禁用临时适配器 |
| 本地启动日志 | `<仓库根>/.runtime/local-logs/backend-<启动时间>.<日期>.<分片>.log`、`frontend-<启动时间>.<日期>.<分片>.log`；旧版文件为 `.{out,err}.log` | `scripts/start-local.ps1` 通过轮转转发脚本生成，按日及 10 MB 大小切片；旧版与新版过期文件均清理 |
| 规划新增应用日志 | `FINANCE_AGENT_LOG_ROOT`，local 建议 `<仓库根>/.runtime/app-logs`，Compose 建议 `/opt/finance-agent/.runtime/app-logs`（同一 runtime volume） | Logback 继续写结构化控制台，同时将应用与清理记录写为独立、按日及大小滚动的文件；保留 30 天，Agent 可直接读文本日志 |
| 调度配置 | `FINANCE_AGENT_SCHEDULE_ROOT`；默认 `<后端工作目录>/.runtime/scheduled-jobs`，本地启动脚本设为 `<仓库根>/.runtime/scheduled-jobs`，Compose 为 `/opt/finance-agent/.runtime/scheduled-jobs`（同一 runtime volume） | 保存 `retention-cleanup.json` 的频率及执行时间；配置数据，不参与清理 |
| Docker 容器 stdout/stderr | 容器运行时管理，当前 Compose 未挂载此类日志目录 | 应用任务不遍历/删除宿主机或 Docker 管理的日志；部署平台自行配置轮转 |

文件清理规范化每个配置根路径，确认真实路径仍在根下，只处理已知的文件名或工作区身份布局，当前修改时间未到期的活动文件予以保留；超过 30 天的遗留上传临时文件也清理。Workspace 扫描必须排除嵌套的 `objects` 根，再由临时对象清理器单独处理。滚动日志由 Logback 的保留配置清理；本地启动脚本产生的日志按其自身轮转/清理。应用与前端调试输出应继续可从本地文件直接读取；避免因增加 Logback 文件 appender 而无上限重复积累本地脚本重定向文件。

### 4. 调度、日志与页面

单体进程使用 Spring 定时调度，默认每周日 02:00，显式指定 `Asia/Shanghai`。受控配置文件 `<工作目录>/.runtime/scheduled-jobs/retention-cleanup.json` 只保存固定清理任务的频率（每天、每周、每月）、星期或月日期及时分；每月日期限定 1–28 日，以确保每月都有执行日，不向用户开放任意 cron 表达式；管理员更新时原子替换配置文件并取消旧的下一次触发，立即按新时间计算下一次运行。任务正在运行时允许修改后续计划，不中断本次清理；进程重启从文件恢复。该配置目录不属于清理目录。每次运行固定截止时间，按表、文件类别计数，记录开始/结束、成功/失败及脱敏错误摘要到专用清理日志；Logback 按日期和大小滚动，30 天保留，并设置总容量上限。管理员 API 仅读取受控清理日志目录中的最近记录，固定数量和最大字节数，不接收任意文件路径；沿用 `PLATFORM_ADMIN` + `CONFIG_MANAGE` 访问约束。前端复用现有导航与组件，左侧列出任务，右侧在同一行显示 Java 类名和调度设置，下方显示最新一次日志，空日志状态不撑高卡片；当前只有“业务数据清理”一项，无手动执行入口。

## Risks / Trade-offs

- **旧父记录有较新引用者** → 连带删除引用者是用户选择的规则；实施测试必须覆盖并在日志计数中显式体现。
- **按 `updated_at` 清理时各表活动时间不同** → 每表独立判定并在依赖删除时处理连带记录；页面可能在一段时间内缺少旧历史子项，属既定逐表规则。
- **审计/已发布修订保留语义改变** → 通过迁移和 delta spec 明示；仅清理事务可删，平常业务路径仍不可改删。
- **审计链跨越保留窗口** → 新事件原有的前序摘要保持不变；已删除事件不可再查询或追溯。
- **文件路径可配置** → 只在应用受控的 Workspace、临时对象和日志目录内操作，拒绝越界与不认识的条目；目录不存在时记为零项。
- **数据库文件不立即缩小** → PostgreSQL 可复用已删除行空间；不执行阻塞性的全库压缩。

## Migration Plan

1. 先完成审计触发器受控删除迁移和相关仓储；保留原有普通操作的不可变约束。
2. 接入有界表级清理、受控文件目录清理、日志轮转及管理员只读页面；新调度在完成验证后才启用。
3. 本地由用户启动并手动核对清理日志；实施者只做静态检查、编译和自动化测试。回退应用版本时停用新调度；已删除数据不可由应用回滚，故首轮调度前验证时间字段、计数和依赖顺序。

## Open Questions

无待定业务决策。实施时以迁移和目标环境实际 schema 复核清单；发现新增表、目录或路径差异时先更新本设计，不扩大清理范围。
