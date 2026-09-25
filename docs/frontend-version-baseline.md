# 前端版本基线

## 已锁定版本

| 项目 | 版本 |
| --- | --- |
| Node.js | 24.16.0 |
| npm | 11.13.0 |
| Vue | 2.7.16 |
| Vue Router | 3.6.5 |
| Element UI | 2.0.0 |
| Vite | 4.5.14 |
| Vite Vue plugin | `@vitejs/plugin-vue2` 2.3.4 |
| TypeScript | 5.7.3 |
| Axios | 1.20.0 |
| Vue SFC compiler | 2.7.16（随 Vue 2.7 工具链） |

版本由 `frontend/package.json`、`frontend/package-lock.json` 和 `frontend/.nvmrc` 共同锁定。验证命令为 `npm ci`、`npm run typecheck` 和 `npm run build`。

## EOL 例外

Vue 2.7.16 与 Element UI 2.0.0 已停止维护，但当前方案仍要求 Vue 2/Element UI 兼容体系，因此本阶段保留该组合作为基线。Vue 2.7.16 使用官方 `@vitejs/plugin-vue2`，不再依赖仅支持 Vue 2.6 的社区插件。例外范围仅限工程骨架和后续 P0 工作台；依赖版本必须继续锁定，不得引入 Vue 3 或第二套 UI 组件库。后续迁移需单独评估模板编译、路由、组件和契约类型边界。

前端只读取构建时公开的 `VITE_*` 配置，不在源码或构建产物中保存 API Key、数据库密码、开发身份令牌或用户数据。

当前 `npm audit --omit=dev` 报告 Vue 2.7.16 及其 Element UI 依赖链的 2 项低风险问题；可用修复会切换到 Vue 3/其他组件体系，因此不在本任务中自动升级，后续迁移议题需一并复评。
