## Context

仓库已有 `frontend/` 目录但没有工程文件；根目录 Node 版本为 24.16.0、npm 11.13.0。仓库技术基线明确使用 Vue 2.7.16、TypeScript、Vite、Element UI 2.0.0 和 Vue Router 3，且 AGENTS.md 要求复用现有组件体系、禁止 Vue 3 专属 API、用户提示使用中文。本任务只建立可构建骨架，不接入真实 API、权限或开发身份。

## Goals / Non-Goals

**Goals:**

- 建立可由 `npm ci` 复现的 Vue 2.7.16 + TypeScript + Vite 工程。
- 提供基础布局、默认路由、Element UI 入口和中文错误/空状态。
- 将 HTTP、SSE 和生成类型分别放入清晰边界，供后续业务任务复用。
- 锁定运行时与依赖版本，并记录 Vue 2/Element UI EOL 例外。

**Non-Goals:**

- 不实现业务页面、后端 API、权限校验、登录或真实身份。
- 不引入第二套 UI 组件库，不生成或保存任何密钥。
- 不启动 Vite 开发服务器或执行人工浏览器验证。

## Decisions

1. **使用 Vite 4.5.14 与官方 `@vitejs/plugin-vue2` 2.3.4。**
   - 选择原因：官方插件要求 Vue 2.7 及以上，并与 Vite 4 和 Node 24 兼容，适配已调整的 Vue 2.7.16 基线。
   - 替代方案：仅支持 Vue 2.6 的社区 `vite-plugin-vue2` 不再适用；Vue CLI 已停止维护，无法满足 Vite 基线，均放弃。

2. **固定 Vue 2.7.16 与同版本 SFC 编译能力。**
   - 选择原因：与仓库更新后的技术基线一致，避免 Vue 运行时和 SFC 编译器跨小版本不匹配。
   - 替代方案：Vue 2.6.10 需要社区 Vite 插件并保留更高的 EOL 风险，已按用户决定切换。

3. **使用 Axios 与原生 `EventSource`，统一放在 `src/services`。**
   - 选择原因：HTTP 拦截和 SSE 生命周期可集中处理，骨架不提前绑定后端契约；原生 EventSource 无需额外 polyfill。
   - 替代方案：在组件中直接请求，会造成 URL、错误处理和重连策略分散，放弃。

4. **生成类型只允许落在 `src/api/generated`。**
   - 选择原因：后续从 `contracts/` 生成 TypeScript 类型时有单一边界，手写视图模型不与生成物混放。

5. **Node/npm 采用仓库当前验证版本并通过 `.nvmrc` 与 engines 记录。**
   - 选择原因：Node 24.16.0/npm 11.13.0 已在工作区可用，能直接执行 `npm ci` 和生产构建。

## Risks / Trade-offs

- [Risk] Vue 2.7.16 和 Element UI 2.0.0 已停止维护 → 在版本说明记录 EOL 例外、锁定依赖并将迁移列为后续议题。
- [Risk] Node 24 与旧版 Vue 生态存在边缘兼容性 → 使用 Vite 4.5.14、锁文件和 CI 同等命令验证。
- [Risk] 原生 EventSource 不支持自定义请求头 → 当前只提供骨架接口；认证和带凭据 SSE 策略留给后续契约任务。

## Migration Plan

1. 在 `frontend/` 执行 `npm ci`、`npm run typecheck`、`npm run build`。
2. 后续业务任务只从 `src/services` 调用 HTTP/SSE，从 `src/api/generated` 引用生成类型。
3. 若需迁移 Vue 版本，先更新版本说明和兼容性验证，再调整依赖；回滚可恢复本任务锁文件和配置。

## Open Questions

无。后续 API 契约、身份和权限在 DEV-BOOT-006 及工作台任务中定义。
