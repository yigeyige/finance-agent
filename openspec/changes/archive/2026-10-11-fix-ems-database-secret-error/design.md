# Design

缺少主密钥不是业务状态变化，应由 `DomainException` 携带 `CONFIGURATION_UNAVAILABLE` 错误码，以 503 和安全中文信息返回。主密钥仍由环境变量注入；本地修复通过生成足够长度的随机值写入忽略的 `.env`，重启后才生效。
