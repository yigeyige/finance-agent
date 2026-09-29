# DeepSeek 真实模型手动验证

默认命令不访问网络，也不会读取 `DEEPSEEK_API_KEY`：

```powershell
pwsh -NoProfile -File scripts/live-deepseek-test.ps1
```

用户确认本机可联网、已设置临时进程变量后再执行真实调用：

```powershell
$env:DEEPSEEK_API_KEY = '<仅当前终端使用的密钥>'
pwsh -NoProfile -File scripts/live-deepseek-test.ps1 -Execute
Remove-Item Env:DEEPSEEK_API_KEY
```

脚本只保存模型名、响应 ID、finish reason、token 计数和结构化 schema 状态，不保存 prompt、response 原文、reasoning、Authorization 或 API Key。结果模板为 `scripts/fixtures/deepseek-live-result-template.json`；请将人工结论和限制记录在本地，不要把真实结果、密钥或业务数据提交到仓库。

人工验证至少记录：模型、token 计数、schema 是否满足、完成原因、网络/限流限制和用户结论。脚本不会启动应用或 Worker，也不会替代用户对费用、权限和真实业务影响的确认。
