# 契约样例

文件名第一段绑定 schema，例如 `task.*`、`step-approval.*`、`business-scene-skill-request.*`、`model-connection-config-request.*`、`event.*`、`decision-packet.*`、`error-response.*`。

- `*.valid.json`：必须通过校验。
- `*.invalid.json`：必须被校验器拒绝；文件名说明负向场景。

样例只使用合成 UUID、时间和文本，不包含真实用户、账号、密钥或连接串。
