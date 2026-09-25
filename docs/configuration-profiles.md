# 配置与启动校验

## Profile

后端默认使用 `local` profile。可通过 `SPRING_PROFILES_ACTIVE` 切换到 `test` 或 `prod`：

- `local`：允许 Stub 模型、本地工作目录和本地存储，便于开发。
- `test`：使用测试端口、测试工作目录和内存存储，允许 Stub 模型。
- `prod`：要求真实数据库、DeepSeek、工作目录、身份和对象存储配置，并在启动前拒绝开发模式。

## 环境变量

配置文件中的 `finance-agent` 命名空间可由以下环境变量覆盖：

| 环境变量 | 用途 |
| --- | --- |
| `FINANCE_AGENT_PORT` | HTTP 端口 |
| `FINANCE_AGENT_DB_URL` | PostgreSQL JDBC URL |
| `FINANCE_AGENT_DB_USERNAME` / `FINANCE_AGENT_DB_PASSWORD` | 数据库账号与密码 |
| `DEEPSEEK_API_KEY` / `DEEPSEEK_BASE_URL` / `DEEPSEEK_MODEL` | DeepSeek 访问配置 |
| `FINANCE_AGENT_DEEPSEEK_STUB` | 是否启用 Stub 模型（生产必须为 `false`） |
| `FINANCE_AGENT_WORKSPACE_ROOT` | 工作目录 |
| `FINANCE_AGENT_MAX_STEPS` / `FINANCE_AGENT_MAX_TOKENS` / `FINANCE_AGENT_MAX_COST` | Agent 执行预算 |
| `FINANCE_AGENT_IDENTITY_MODE` | 身份来源（生产不能为 `test` 或 `stub`） |
| `FINANCE_AGENT_STORAGE_MODE` | 存储类型（生产不能为 `local` 或 `memory`） |
| `FINANCE_AGENT_FIELD_STRICT` | 严格字段策略（生产必须为 `true`） |

根目录 `.env.example` 仅提供变量名和示例安全默认值。应用不会读取 `.env` 文件，真实值必须由运行环境注入，也不得提交到仓库。

## 生产启动校验

激活 `prod` profile 后，启动校验器会检查数据库 URL、用户名、密码，DeepSeek API Key 和 Base URL、工作目录是否存在，并拒绝 Stub 模型、测试身份、本地/内存存储和宽松字段策略。失败信息只列出配置项名称，不包含 API Key、数据库密码或完整连接串。
