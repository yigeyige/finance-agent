## Why

`DEV-HAR-007` 需要把已锁定的 AgentScope OpenAI 兼容扩展接入 DeepSeek，同时保持默认测试离线、凭据只从环境变量注入，并把真实模型调用留给用户显式触发。

## What Changes

- 固化 DeepSeek Chat Completions 配置默认值与手工 live-test profile。
- 增加无自动切换的 DeepSeek OpenAI 兼容模型适配器，复用 AgentScope 2.0.3 OpenAI 扩展。
- 以 AgentScope `GenerateOptions` 承载结构化输出、工具调用、超时和模型元数据边界；取消通过 Reactor 订阅取消传播。
- 增加 HTTP Stub 协议契约测试和不暴露密钥的配置/失败行为测试。

## Non-goals

- 不在默认测试中访问真实 DeepSeek，不提交或打印 API Key。
- 不实现模型自动切换、重试策略或持久化 reasoning 内容。
