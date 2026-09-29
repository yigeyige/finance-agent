# 统一认证适配器细化任务

本文是 `DEV-P1-003` 的实现输入，依赖身份平台完成 [统一认证身份契约](identity-auth-contract.md) 的待签署项；在签署前不得接入真实生产 issuer。

## 实施拆分

1. 实现统一 `AuthenticatedPrincipal` 领域对象，禁止向上层暴露原始 token。
2. 在基础设施层实现 OIDC/JWKS 校验适配器，校验 `iss`、`aud`、签名、`exp`、`nbf` 和环境 allow-list。
3. 实现 `employee_no` 映射、禁用/撤销检查和角色同步边界；补齐唯一性与失效审计。
4. 为 `local`、`test`、`prod` 提供独立配置入口和启动校验，阻断跨环境 issuer/audience。
5. 增加合成正反样本测试、token 过期/撤销测试和敏感字段日志断言。
6. 按 break-glass 流程增加受控开关、审批与审计记录，默认关闭。

## 阻断条件

- 任何真实 issuer、audience、JWKS 或人员编号格式未签署时，保持 stub/合成身份。
- 不允许通过请求体、查询参数或前端字段指定操作者身份。
- 不允许将 access token、完整 claims、JWKS 响应或凭据写入日志、快照、模型请求或向量索引。
- 不允许 test/local 身份命中 prod 配置，也不允许 prod token 在 test/local 适配器中复用。

## 交付证据

- 身份平台签署的契约版本和环境参数清单。
- 合成映射正反样本测试报告。
- 适配器配置校验、撤销/禁用和 break-glass 审计测试结果。
