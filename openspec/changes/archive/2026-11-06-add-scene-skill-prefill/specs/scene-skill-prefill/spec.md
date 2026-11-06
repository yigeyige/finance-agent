## ADDED Requirements

### Requirement: Scene prefill generates one prompt target at a time

系统 SHALL 提供 `POST /api/v1/business-scene-skills/prefill`，按 `target`（`INVESTIGATION`、`DECISION_PACKET`、`PROCESS_ARTIFACT`）生成对应的场景提示词，不创建或修改场景。`INVESTIGATION` MUST 同时返回场景名称；另外两个 target MUST 只返回提示词内容。请求 MUST 不包含未知字段，模型调用超时 MUST 在 60 秒内结束。

#### Scenario: Generate investigation prompt

- **WHEN** 场景维护者提交业务描述并选择调查目标
- **THEN** 系统返回场景名称与调查提示词，并按规则筛选表名后交给模型

#### Scenario: Decision or process target lacks prerequisite

- **WHEN** 生成决策包提示词时没有调查提示词，或生成流程文件提示词时没有决策包提示词
- **THEN** 系统返回 400 与中文提示，且不调用模型

#### Scenario: Non-maintainer edits an existing scene

- **WHEN** 非创建人且非平台管理员的用户携带 `skillId` 请求预填
- **THEN** 系统返回 403

### Requirement: Table names are filtered deterministically before model calls

系统 SHALL 按以下顺序筛选所选实例的表名：文本中出现已知 schema 或 `schema.table` 时先限定 schema；再排除表名中作为独立片段的日志、临时、备份、历史、MQ、消息队列、事务类表，以及带 6/8 位日期或 `YYYY_MM_DD` 后缀的表；其余表名 MUST 全部传给模型。筛选后为空时 MUST 继续生成并返回“未使用表结构”的提示。

#### Scenario: Schema mentioned in text narrows candidates

- **WHEN** 业务描述中出现 `basedata` 等已知 schema
- **THEN** 只保留这些 schema 下的表参与模型上下文

#### Scenario: No tables remain after filtering

- **WHEN** 排除规则过滤掉全部表
- **THEN** 仍生成内容，并在响应中给出未使用表结构的提示与筛选统计

### Requirement: Scene editor uses a full-content form with explicit prefill apply

业务场景的新建/编辑 MUST 在右侧内容区以全页表单展示（侧栏保持可见），标题右侧提供「AI 预填」与「返回」，返回时存在未保存修改 MUST 二次确认。AI 预填 MUST 使用弹窗，包含业务描述、数据库实例下拉（可不选）与三个生成按钮；每次生成 MUST 先展示对应字段的只读预览，应用时调查目标回填场景名称与调查提示词，另外两个目标只回填各自字段。

#### Scenario: Apply investigation result

- **WHEN** 用户生成调查提示词并点击应用
- **THEN** 场景名称与调查提示词被回填，其他字段保持不变

#### Scenario: Leave form with unsaved changes

- **WHEN** 用户在表单有未保存修改时点击返回
- **THEN** 系统先要求确认，确认后才丢弃修改并回到列表
