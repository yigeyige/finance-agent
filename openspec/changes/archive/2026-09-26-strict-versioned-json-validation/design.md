## Context

`VersionedJson` 是任务范围模型中的值对象，当前只通过首尾字符判断 JSON 是否为 object 或 array。该检查无法识别语法错误或尾随 JSON 内容。项目的 Spring Boot Web 依赖已提供 Jackson，且本次不需要新增依赖。

## Goals / Non-Goals

**Goals:**

- 使用 Jackson 对 JSON 文本进行完整语法解析。
- 拒绝非 object/array、语法错误和尾随内容。
- 保持领域对象的构造约束、`requireObject()` 行为和 `IllegalArgumentException` 异常边界。
- 在后端编码规范中固化“优先复用现有能力，新增组件先确认”的规则。

**Non-Goals:**

- 不改变 JSON 内容的业务 schema 校验规则。
- 不引入新的 JSON 库或 Maven 依赖。
- 不改变数据库 JSONB 映射和 API 契约。

## Decisions

- 使用 Jackson `ObjectMapper.readTree` 作为语法解析入口，并启用 `FAIL_ON_TRAILING_TOKENS`，避免仅解析首个 JSON 值。
- 在 `VersionedJson` 内部复用一个专用于结构校验的 Jackson mapper；解析结果只用于判断 object/array，不保存额外 `JsonNode` 状态，保持值对象结构不变。
- 将 Jackson 解析异常转换为现有领域边界使用的 `IllegalArgumentException`，保留原始异常作为 cause，避免向调用方暴露 Jackson 具体异常类型。
- 通过单元测试覆盖合法 object、合法 array、非法 JSON、尾随内容和 `requireObject()` 边界。

替代方案：继续手写括号检查无法保证 JSON 语法；引入其他 JSON 库会增加依赖和配置分裂；把解析下沉到基础设施会使领域值对象在未解析时仍可构造，不能保证不变量。

## Risks / Trade-offs

- [Jackson 默认配置变化] → 使用本类专用 mapper 并显式启用尾随 token 检查，避免依赖全局配置。
- [领域层依赖 Jackson API] → 仅依赖 Jackson 的数据绑定解析能力，不依赖 Spring 容器或 Web 层；项目已有 Jackson 依赖，不新增外部组件。
- [非法历史数据无法重新构造] → 这是预期的严格化行为；数据库读取和写入边界继续由现有领域校验保护。
