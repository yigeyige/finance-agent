# 统一认证身份契约（P1 冻结稿）

> 状态：`CONDITIONAL`。本文冻结 Agent 侧接口形状和安全边界；真实身份平台签署前，协议、issuer、audience 和 claims 的生产值保持待确认，不得用示例值替代。

## 1. 契约边界

| 项目 | Agent 侧约束 | 真实平台输入 |
| --- | --- | --- |
| 协议 | OIDC/OAuth 2.0 Bearer access token；API 不接受请求体中的操作者身份 | 身份平台确认授权类型和网关传递方式 |
| issuer | 必须精确匹配已批准的 issuer allow-list | local/test/prod 各环境 issuer URL |
| audience | 必须包含 Agent API 的批准 audience | 各环境 audience 值 |
| 签名与时间 | 使用平台 JWKS 校验签名、`exp`、`nbf` 和时钟偏差 | JWKS 地址、轮换窗口和最大时钟偏差 |
| 唯一身份 | 使用不可变 `auth_subject` 关联唯一 `employee_no`；禁止以姓名、邮箱作为主键 | 身份平台提供唯一人员编号和生命周期语义 |

## 2. Claims 最小集合

适配器只读取完成授权所需的最小字段，并将原始 token 与未批准 claims 排除在日志、任务快照和模型输入之外。

| Claim | 必填 | 用途 |
| --- | --- | --- |
| `iss` | 是 | issuer 白名单匹配 |
| `aud` | 是 | audience 匹配 |
| `sub` | 是 | 保存为 `auth_subject`，在同一 issuer 下唯一 |
| `employee_no` | 是 | 映射 Agent 用户的唯一人员编号；真实 claim 名称待平台确认 |
| `exp` / `nbf` | 是 | 有效期和生效时间校验 |
| `roles` | 否 | 仅作为角色同步输入，不能绕过 Agent 内角色维护 |
| `name` / `email` | 否 | 展示用途，不参与身份唯一性 |

如果平台使用不同 claim 名称，必须在环境适配器配置中显式映射并由身份平台签署；缺少 `sub` 或唯一人员编号时拒绝请求。

## 3. 角色同步、禁用与撤销

- Agent 自有库维护 `OPERATOR`、`VIEWER`、`PLATFORM_ADMIN` 角色；统一认证只提供身份和已批准的角色同步输入。
- 每次请求都重新校验 token 有效期和撤销状态；角色变更采用短缓存或下一请求生效，不能永久信任历史 token。
- 人员编号变更不得覆盖历史审计主体；应创建新的映射并保留旧映射的失效时间。
- 禁用、离职或撤销后，新的请求必须拒绝；已有任务按任务状态机进入安全检查点，不扩大权限。

## 4. 环境适配器边界

| 环境 | 允许身份 | 适配器约束 |
| --- | --- | --- |
| `local` | 合成身份 | 仅本地 profile；明确标记 `synthetic=true`，不得连接 prod issuer |
| `test` | 测试租户/合成身份 | 只接受 test issuer、audience 和人员编号；测试身份不得进入 prod 数据或 token allow-list |
| `prod` | 真实统一认证 | 仅接受已签署的 issuer、audience、JWKS 和人员编号 claim；缺失配置 fail-fast |

环境选择由部署配置决定，不由请求体或 token claim 决定。适配器接口应统一返回 `AuthenticatedPrincipal(authSubject, employeeNo, roles, environment)`，具体 OIDC 客户端和网关差异留在基础设施层。

## 5. Break-glass 流程

1. 仅限平台管理员在统一认证不可用且经批准的维护窗口启用。
2. 使用独立、短时、一次性凭据；不得把 break-glass 作为普通登录或长期服务账号。
3. 启用、使用、失效和复盘均写入审计，至少记录工单号、批准人、操作者、开始/结束时间和操作范围；不记录凭据内容。
4. 访问范围限定为诊断、恢复和配置核对，不得直接写入财务源系统或绕过发布/审批门禁。
5. 维护结束立即撤销；若无法撤销，必须按安全事件流程升级。

## 6. 合成映射样本

以下仅用于契约测试，不代表真实身份：

| 环境 | `iss` | `aud` | `sub` | `employee_no` | 预期 |
| --- | --- | --- | --- | --- | --- |
| local | `https://identity.local.example` | `finance-agent-api-local` | `sub-local-001` | `SYNTH-0001` | 接受并标记合成身份 |
| test | `https://identity.test.example` | `finance-agent-api-test` | `sub-test-001` | `SYNTH-0002` | 接受测试租户身份 |
| prod | `https://identity.prod.example` | `finance-agent-api-prod` | `sub-prod-001` | `SYNTH-0003` | 仅在平台签署后接受 |
| prod | `https://identity.test.example` | `finance-agent-api-prod` | `sub-test-001` | `SYNTH-0002` | 拒绝跨环境身份 |
| test | `https://identity.test.example` | `finance-agent-api-test` | `sub-test-002` | 空 | 拒绝缺少唯一人员编号 |

## 7. 待身份平台签署项

- [ ] 确认协议、issuer、audience、JWKS、时钟偏差和撤销查询方式。
- [ ] 确认唯一人员编号 claim 名称、格式、变更和离职语义。
- [ ] 确认角色同步来源、缓存上限和管理员维护流程。
- [ ] 确认 break-glass 工单、审批人、凭据生命周期和审计留存。

