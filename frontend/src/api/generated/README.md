# 生成类型边界

本目录只存放由 `contracts/` 中版本化契约生成的 TypeScript 类型和客户端代码。当前仓库尚未接入生成器，`index.ts` 是与版本化契约对齐的临时生成边界，所有契约字段和枚举变更必须先更新该边界并通过 `npm run typecheck`。手写的请求封装、视图模型和错误处理放在 `src/services`、`src/api` 或 `src/types`，不得把请求逻辑放进页面。
