## Context

`DeepSeekOpenAiAdapter` 已使用 AgentScope 的 OpenAI 兼容客户端、`deepseek-flash` 默认模型、结构化输出和 thinking 过滤。当前缺少面向用户的真实调用命令；脚本应复用同一协议约束，但不能绕过适配器的生产代码。

## Decisions

1. 默认模式只检查脚本和模板，不读取 API Key、不发网络请求；`-Execute` 才读取进程环境中的 `DEEPSEEK_API_KEY`。
2. 请求固定为合成最小 JSON object，`stream=false`、结构化输出和有限 token；脚本只输出 HTTP 状态、模型、响应 ID、usage、finish reason 和 schema 是否满足。
3. 脚本永不输出或保存请求 prompt、response body、reasoning、Authorization header 或 API Key；结果模板由用户手动补充非敏感限制和结论。
4. 真实运行、费用、限流和网络失败由用户负责；AI 只做静态检查、Stub 测试和编译。
