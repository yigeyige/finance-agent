## Why

任务已有证据、覆盖度和专业确认结果，但还不能形成可审阅的决策包草稿。需要以 canonical JSON 作为权威内容，并生成可追溯的 HTML 快照，供后续复核和发布使用。

## What Changes

- 新增结构化章节组装器和草稿 validator，校验事实时效分类、依据章节、业务影响显式确认和验证准备。
- 新增草稿 API，写入范围/运行版本绑定，返回 canonical JSON、digest 和 HTML 快照。
- 新增前端草稿表单和 JSON/HTML 预览，不实现 PDF。

## Capabilities

### New Capabilities
- `packet-draft-assembly-ui`: 决策包草稿组装、校验和 HTML 快照。

### Modified Capabilities

## Impact

- 影响 packet 应用服务、结构化 validator、草稿 REST 控制器和前端页面。
- 复用既有 packet 草稿仓储、范围/运行版本校验和任务审计摘要链。
- 不新增依赖，不允许自由文本覆盖事实字段或从配置推断业务影响。
