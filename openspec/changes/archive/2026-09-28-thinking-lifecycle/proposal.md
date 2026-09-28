## Why

DeepSeek 的 thinking/reasoning 内容只用于当前模型工具循环，不能成为用户可见事件、异常、日志、checkpoint 或数据库事实。当前已有 OpenAI 兼容适配器，但缺少统一的 `reasoning_content` 过滤和易失生命周期边界，容易在协议映射或持久化时误留敏感推理内容。

## What Changes

- 增加 thinking high 的默认调用选项和 DeepSeek `reasoning_content` 协议映射。
- 增加仅驻留内存的瞬时 reasoning 容器、响应过滤器和工具循环生命周期清理钩子。
- 过滤事件、异常、日志、checkpoint、数据库和 SSE 输出中的 reasoning 字段；仅保留最终回答、工具调用和非敏感完成元数据。
- 增加 HTTP Stub 契约与全链路禁止字段扫描测试，不执行真实模型调用。

## Capabilities

### New Capabilities

- `thinking-lifecycle`: 定义 reasoning 内容的协议映射、过滤和易失内存生命周期。

### Modified Capabilities

- `deepseek-adapter`: 增加默认 thinking high 和 reasoning 字段的传递约束。

## Impact

- 影响 `harness/infrastructure` 的 DeepSeek 适配器及 `harness/application` 的非持久模型响应边界。
- 增加测试用 HTTP Stub 和敏感标记扫描；不引入新依赖，不改变默认离线测试和真实 live-test 手动触发边界。
