## Context

仓库已经建立 Java 模块和 Vue 工作台骨架，但 `contracts/` 目录尚未形成可验证的源契约。方案附件 B 规定 REST `/api/v1`、统一错误模型、任务/审批/事件/决策包对象及生成边界；当前任务负责冻结最小 P0 契约，不提前实现业务服务。

## Goals / Non-Goals

**Goals:**

- 用 JSON 文件表达 OpenAPI 3.1，避免本机缺少 YAML 解析器阻塞校验。
- 为任务、审批、事件、决策包和错误响应提供独立、带 `$id` 与版本的 JSON Schema。
- 提供可复现的正负样例和标准库校验脚本，覆盖关键负向门禁。
- 明确契约源、Java/TypeScript 生成目录和禁止手工复制 DTO 的边界。

**Non-Goals:**

- 不实现 Spring Controller、Java DTO、TypeScript 业务类型或 OpenAPI 服务器。
- 不绑定真实源系统字段；真实源字段继续使用 `TBC-SRC-*` 占位。
- 不引入网络下载型校验器或修改现有 Maven/npm 依赖。

## Decisions

1. **OpenAPI 文档使用 JSON 而非 YAML。**
   - 选择原因：JSON 可由 Python 标准库解析，减少开发环境依赖，同时符合 OpenAPI 3.1。
   - 替代方案：YAML 可读性更好，但当前仓库未锁定 YAML 解析器，校验不可复现，暂不采用。

2. **JSON Schema 使用 Draft 2020-12，所有 `$id` 以 `schema://` 和语义版本结尾。**
   - 选择原因：与方案附件 B 一致，能表达 `format`、`enum`、边界和对象封闭策略。
   - 替代方案：在 OpenAPI 中重复维护内嵌 schema，会导致生成边界不清晰，放弃。

3. **校验脚本使用 Python 标准库实现最小验证器。**
   - 选择原因：无需安装 `jsonschema`，可在 Windows 与 CI 直接运行；脚本只覆盖本任务冻结的关键关键字。
   - 替代方案：依赖第三方验证器能覆盖更多 Draft 特性，但需要额外锁定运行时依赖，留待后续契约测试任务。

4. **示例文件通过文件名绑定 schema。**
   - 选择原因：`task.valid.json`、`task.extra-field.invalid.json` 等命名直观，脚本无需维护另一份 manifest。

5. **生成方向单向流动。**
   - `contracts/` 是唯一源；Java 生成/校验产物进入后端生成目录，TypeScript 生成产物进入 `frontend/src/api/generated`；手写服务只能引用生成类型。

## Risks / Trade-offs

- [Risk] 标准库验证器不是完整 JSON Schema 实现 → 明确支持的关键字，并在后续引入正式校验器时保留样例作为回归集。
- [Risk] OpenAPI 与独立 schema 的 `$ref` 路径漂移 → 脚本检查路径存在且所有独立 schema 可直接验证。
- [Risk] 领域字段过早冻结 → 只冻结 P0 最小字段，真实源字段使用 `TBC-SRC-*`，新增字段通过版本化变更进入。

## Migration Plan

1. 执行 `scripts/verify-contracts.ps1` 或 `python scripts/verify-contracts.py`。
2. 后续生成任务从 `contracts/` 读取源文件，不直接修改生成目录。
3. 契约变更新增版本 `$id` 和样例；旧版本保留直到调用方迁移完成。

## Open Questions

无。具体生成器选型在 DEV-UI-001 和后续后端 API 任务中确定。
