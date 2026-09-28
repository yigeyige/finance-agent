## Context

仓库已经锁定 AgentScope Java 2.0.3 及 `agentscope-extensions-model-openai`。该扩展直接支持 OpenAI Chat Completions、工具 schema 和原生结构化输出，因此适配器只负责安全配置、默认值和模型实例生命周期，不重复实现 HTTP 协议。

## Decisions

- `DeepSeekOpenAiAdapter` 使用 `OpenAIChatModel.builder()`，固定 endpoint `/chat/completions`，默认 base URL `https://api.deepseek.com`、模型 `deepseek-flash`。
- API Key 只作为 builder 输入，不写入日志、异常、元数据或配置说明；没有 key 时工厂拒绝构建可调用模型。
- 默认配置和普通 Maven 测试只创建 Stub/配置对象，不执行真实请求；live profile 使用 `DEEPSEEK_API_KEY`，由用户手动触发。
- timeout 通过 `ExecutionConfig` 传递，取消通过 Reactor `Flux` 的订阅取消语义生效；适配器不吞掉模型错误，也不切换模型。
- 结构化 schema 和工具 schema 交给 AgentScope `GenerateOptions` / `ToolSchema`，响应模型、token、finish reason 由 `ChatResponse` 原样提供给上层的非敏感元数据映射。
