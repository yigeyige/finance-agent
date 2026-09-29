## Why

真实 DeepSeek 调用必须由用户在本地持有 API Key 后手动验证；仓库需要提供单独、最小、默认离线的命令和不含敏感内容的结果模板，避免把 prompt、response、reasoning 或 API Key 带入日志和提交。

## What Changes

- 新增 `live-deepseek-test.ps1`，默认只做配置/模板静态检查，显式 `-Execute` 才调用 DeepSeek OpenAI 兼容接口。
- 新增最小合成请求和安全结果模板，仅保留模型、finish reason、token 计数和 schema 状态。
- 新增 live-test 指南，说明用户自行设置 `DEEPSEEK_API_KEY`、执行命令和填写限制/结论。
- 新增后端契约测试，校验 HTTP Stub 契约和敏感响应不落盘。

## Capabilities

### New Capabilities

- `deepseek-live-verification`: 提供真实模型的用户手动验证入口和安全记录格式。

### Modified Capabilities

- 无

## Impact

- 新增脚本、指南、结果模板和契约测试。
- 不在 CI、默认测试或实现方环境中调用真实网络，不要求仓库内保存 API Key。
