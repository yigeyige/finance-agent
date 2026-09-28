## ADDED Requirements

### Requirement: Current-task allowed context

系统 SHALL 只从当前 task 的字段策略裁剪证据、任务元数据和确认字段构造 `AllowedContext`，并提供证据 ID、字段清单摘要和整体摘要。

#### Scenario: Approved evidence is projected
- **WHEN** 当前 task 提交已裁剪证据和确认字段
- **THEN** 构造 AllowedContext，并可通过 evidence ID、字段摘要和上下文摘要追溯，不包含原始 prompt

#### Scenario: Cross-task or sensitive evidence is rejected
- **WHEN** 证据属于其他 task、字段未确认、值冲突或包含禁止字段
- **THEN** 上下文构造失败，不进入模型调用

### Requirement: Published prompt template catalog

系统 SHALL 通过版本化模板目录绑定 runtime bundle，只允许已发布用途和模板版本进入模型调用，不把模板原文持久化为调用元数据。

#### Scenario: Published template is resolved
- **WHEN** 已发布 runtime bundle 的 prompt 版本、用途和模板条目一致
- **THEN** 返回已发布目录描述和模板摘要

#### Scenario: Draft or historical template is rejected
- **WHEN** runtime bundle 未发布、版本不一致或模板条目为草稿/已撤回
- **THEN** 模板解析失败，调用方不能继续模型调用

### Requirement: No RAG or memory as current fact

系统 SHALL 不从未发布知识、RAG 或历史记忆自动补充当前 AllowedContext；只有显式传入的已确认证据才能进入上下文。

#### Scenario: Implicit memory is absent
- **WHEN** 构造器未收到显式确认的证据
- **THEN** 不生成上下文，不把历史配置值当作当前事实
