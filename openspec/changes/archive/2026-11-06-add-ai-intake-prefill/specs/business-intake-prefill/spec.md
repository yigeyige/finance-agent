## ADDED Requirements

### Requirement: Business intake prefill parses free text without creating a task

系统 SHALL 提供 `POST /api/v1/business-intake/draft`，接收 10–2000 字的自由文本，调用模型解析出业务标题、业务说明，并在需要时匹配业务场景与数据库实例。该请求 MUST NOT 创建任务、MUST NOT 调用 EMS 数据库、MUST NOT 修改任何既有业务数据。请求 MUST 要求已登录的 `PLATFORM_USER`；模型调用超时 MUST 在 60 秒内结束。

#### Scenario: Operator previews parsed fields

- **WHEN** 已登录操作员提交一段合法的业务描述
- **THEN** 系统返回标题、说明以及匹配到的场景/实例标识与名称，不产生新任务，也不写入业务数据

#### Scenario: Input is too short or too long

- **WHEN** 提交文本少于 10 字或超过 2000 字
- **THEN** 系统返回 400 与中文提示，不调用模型

#### Scenario: Model fails or times out

- **WHEN** 模型调用失败或超过 60 秒未返回
- **THEN** 系统返回 502 或 504 与中文提示，表单内容保持不变

#### Scenario: Unauthenticated request

- **WHEN** 未登录请求访问预填接口
- **THEN** 系统返回 401

### Requirement: Scenario and instance matching is exact and ambiguity is left empty

匹配 MUST 使用 `trim` 后大小写不敏感的名称精确匹配；当没有候选命中或存在多个同名候选时，对应字段 MUST 留空。标题 MUST 截断到 300 字，说明 MUST 截断到 8000 字。模型只返回部分字段时，系统 MUST 只回传已解析出的字段。

#### Scenario: Unique name match

- **WHEN** 模型返回的场景或实例名称在候选列表中唯一匹配
- **THEN** 响应包含对应标识与名称

#### Scenario: Duplicate or unknown name

- **WHEN** 模型返回的名称匹配到多个候选，或没有任何候选匹配
- **THEN** 响应中对应标识与名称为空，且不回显模型建议名称

### Requirement: Prefill results require explicit operator confirmation

业务处理页 SHALL 在“开始模型分析”旁提供「AI 预填」入口；解析结果 MUST 以只读预览展示，只有在操作员点击“应用到表单”后才写入表单。应用时 MUST 只写入非空字段，空字段保留表单原值。

#### Scenario: Operator applies preview

- **WHEN** 操作员在预览弹窗点击“应用到表单”
- **THEN** 非空的标题、说明、场景、实例覆盖表单，解析为空的字段保持原值，弹窗关闭

#### Scenario: Operator closes preview

- **WHEN** 操作员关闭弹窗或点击取消
- **THEN** 表单不发生任何修改
