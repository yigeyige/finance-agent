# 测试来源交叉印证样本

## 固定目录

- 对象类型：`test-personnel-assignment`
- 主来源：`test-business-primary`
- 核验来源：`test-business-verification`
- 权威顺序：主来源优先，核验来源次之。
- 数据边界：所有对象键、来源标识和摘要均为固定虚构测试值；不含真实人员、凭证、金额、生产配置或连接信息。

## 样本语义

| 场景 | 主来源摘要 | 核验来源摘要 | 期望结果 |
|---|---|---|---|
| `MATCHING` | 相同 | 相同 | `RESOLVED`，结果来源为主来源 |
| `CONFLICT` | 不同 | 不同 | `UNKNOWN_SOURCE_CONFLICT`，保留两份候选 |
| `UNKNOWN` | 相同 | 相同 | `UNKNOWN_SOURCE_CONFLICT`，不产生确认事实 |
| `VERIFICATION_UNAVAILABLE` | 存在 | 不可用 | 失败关闭，不回退为主来源确认 |
