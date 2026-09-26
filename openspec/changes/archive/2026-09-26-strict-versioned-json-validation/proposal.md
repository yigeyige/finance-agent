## Why

`VersionedJson` 当前仅检查 JSON 文本的首尾字符，结构非法的内容仍可能进入范围模型。项目已有 Jackson 能力，应复用现有序列化组件进行严格解析，避免继续手写基础能力。

## What Changes

- 使用项目现有 Jackson `ObjectMapper` 严格解析 `VersionedJson` 的 object/array 内容。
- 保持 object/array 领域约束、`requireObject()` 语义和现有异常边界。
- 增加非法 JSON、合法 object/array 及 `requireObject()` 的测试覆盖。
- 更新后端编码规范 Skill：优先复用现有框架或通用组件；引入新框架组件前须先获得用户确认。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `task-scope-step-model`: 范围 JSON 必须通过严格 JSON 解析后才能进入领域模型。

## Impact

- 影响 `backend` 中的 `VersionedJson` 领域值对象及其测试。
- 复用 Spring Boot Web 已提供的 Jackson，不新增 Maven 依赖。
- 更新 `.codex/skills/backend-coding-standards/SKILL.md`，不改变运行时接口契约。
