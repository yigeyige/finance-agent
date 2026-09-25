## Why

当前 `contracts/` 只有占位文件，前后端尚无共同的版本化 API 与领域数据契约。现在建立 OpenAPI 3.1、JSON Schema、示例和校验脚本，可以让后续 Java/TypeScript 生成物从同一源契约产生，并在额外字段、枚举、时间格式和数组边界漂移时尽早失败。

## What Changes

- 新增版本化 `contracts/openapi`、`contracts/json-schema`、`contracts/examples` 目录和说明。
- 定义错误响应、任务、审批、公开事件和决策包 JSON Schema，所有对象默认关闭额外字段。
- 定义最小 OpenAPI 3.1 文档，引用任务、审批、发布和错误模型。
- 明确 Java 端以契约校验/生成 DTO，TypeScript 端生成到 `frontend/src/api/generated`，禁止手工维护重复 DTO。
- 新增无第三方运行时依赖的 Python/PowerShell 契约校验脚本，以及有效/无效样例。

## Capabilities

### New Capabilities

- `versioned-contract-source`: 定义 OpenAPI、JSON Schema、版本标识和生成边界。
- `contract-validation-gate`: 定义有效/无效样例和额外字段、枚举、日期格式、数组边界负向校验。

### Modified Capabilities

无。

## Impact

- 新增 `contracts/` 契约源文件、示例和 `scripts/verify-contracts.*`。
- 后端 Java 和前端 TypeScript 后续任务将依赖这些契约，但本任务不生成业务 DTO 或运行时 API。
- 校验脚本仅使用 Python 标准库，不改变 Maven/npm 依赖。
