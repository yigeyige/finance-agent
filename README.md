# 财务共享智能运维 Agent

本仓库用于建设面向财务共享系统功能运维人员的受控调查 Agent。首期能力只读取获准的配置事实，形成可追溯、可复核、可交接的调查决策包；不修改财务系统，不读取真实金额业务数据。

## 仓库目录

| 目录 | 用途 |
|---|---|
| `backend/` | Java 17、Spring Boot 和 AgentScope Java 后端应用 |
| `frontend/` | Vue 2.6.10、TypeScript、Vite 工作台 |
| `contracts/` | OpenAPI、JSON Schema 和契约示例的版本化源文件 |
| `deploy/` | JAR/单应用 Docker 的部署文件和说明 |
| `docs/` | 稳定的开发、依赖、测试和运维说明 |
| `scripts/` | 契约校验、敏感信息扫描和辅助脚本 |
| `solutions/` | 业务方案、生产落地方案和开发任务基线，作为设计依据保留 |
| `tasks/` | 按操作人维护的任务卡和执行记录 |
| `openspec/` | OpenSpec 配置和能力变更工件 |

## 技术基线

- 后端：JDK 17、Spring Boot 4.0.4、AgentScope Java 2.0.3 BOM。
- 前端：Vue 2.6.10、TypeScript、Vite、Element UI 2.x、Vue Router 3。
- 数据库：PostgreSQL + pgvector；核心数据访问使用 Spring JDBC/显式 SQL。
- 部署：可执行 JAR 或单应用 Docker，不使用 Compose，不依赖 Docker socket。

## 开发约定

开发顺序、任务依赖和验收标准以 `tasks/<操作人>/01.doing/task-index.md` 及对应任务卡为准。仓库规范以根目录 `AGENTS.md` 为准；开始开发前确认 `.env` 中的 `CURRENT_USER`，不得提交 `.env`、密钥、密码、生产连接串或真实用户数据。

当前仓库处于工程基线初始化阶段。后续任务按 P0 工程、领域数据、运行时、工具/Harness、工作台、验收，再进入 P1/P2 阶段逐步实施。

## 验证入口

开发命令和责任边界见 [`docs/development-commands.md`](docs/development-commands.md)。修改代码后必须运行任务卡要求的静态检查、编译或测试；长期启动、真实模型调用和人工页面验收由用户执行。
