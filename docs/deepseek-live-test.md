# DeepSeek 手工 live-test

默认 `local`、`test` 配置保持 Stub 模式，不访问真实网络。只有用户明确提供临时 API Key 并指定 `model-live-test` profile 时才创建真实适配器。

PowerShell 示例（不要把 Key 写入仓库、命令历史或日志）：

```powershell
$env:DEEPSEEK_API_KEY = '<由用户临时注入>'
Push-Location backend
try {
    mvn -q -Dspring.profiles.active=model-live-test test
} finally {
    Pop-Location
    Remove-Item Env:DEEPSEEK_API_KEY -ErrorAction SilentlyContinue
}
```

默认模型为 `deepseek-flash`，默认 endpoint 为 `https://api.deepseek.com/chat/completions`。适配器不会自动切换模型；错误或限流会直接结束当前调用，由上层进入暂停/人工处理。

live-test 结果只记录模型名、token、完成原因、schema 校验和耗时等非敏感元数据，不记录 API Key、prompt、response 原文或 reasoning 内容。
