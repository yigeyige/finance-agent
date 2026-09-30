## Context

P1 已收敛为 local/test 的测试业务源试点。当前 `IdentityResolver` 已将白名单测试身份映射到持久化的合成用户和活动角色，`IdentityContextFilter` 负责请求范围内的上下文生命周期，合成种子只在 local/test 且显式开启时注册。该路径不需要真实 issuer、JWT、人员主数据或 break-glass。

目前未知测试标识会返回空；但当用户存在而其所有角色均已撤销时，构造要求至少一个角色的 `CurrentIdentity` 可能抛出异常。该情况应与未知身份一样失败关闭，而不把撤销状态转化为服务器错误。

## Goals / Non-Goals

**Goals:**

- 使 P1 测试认证适配器明确复用现有受控身份路径。
- 将未知身份、prod 测试身份和无活动角色统一处理为无可信身份。
- 用单元测试证明固定角色边界、撤销生效和 prod 隔离，并提供 local/test 操作说明。

**Non-Goals:**

- 不增加 Spring Security、OAuth2、JWT、Token、SSO 或外部身份平台依赖。
- 不改变领域 `AgentUser`、`RoleBinding`、`CurrentIdentity` 模型、数据库迁移或 API 请求格式。
- 不实现生产认证、真实人员映射或 break-glass。

## Decisions

### 1. 复用 `IdentityResolver` 作为测试认证适配器

白名单、环境分支和持久化角色查询已经集中在 `IdentityResolver`，继续以它为唯一适配入口。这样保证 Controller 始终通过 `IdentityContext` 获得操作者，避免在接口层新建第二套身份解析。

备选方案是新增专用测试 Token 过滤器。它重复现有环境与角色边界，还会引入未批准的认证契约，因此不采用。

### 2. 无活动角色返回无身份

保留 `CurrentIdentity` 至少一个角色的不变量；解析器在过滤已撤销绑定后若角色集合为空，则返回 `null`。下游现有 `IdentityContext.require()` 负责产生拒绝，因此不会以空角色身份继续执行，也不会因记录构造异常产生 500。

### 3. 只增加单元测试与操作说明

使用现有 JUnit、Mockito 和 Spring 测试请求对象覆盖解析器分支，测试不连接数据库、不启动应用、不访问网络。运行时启动与人工接口验证继续由用户负责。

## Risks / Trade-offs

- [测试身份头在 local/test 是受控便利入口] → 白名单限定为三个身份，prod 一律不解析，未知或无角色一律为空。
- [返回空身份需要调用方正确拒绝] → 已有 `IdentityContext.require()`，并通过测试验证解析器不提供空角色身份。
- [不能证明真实 SSO 行为] → 真实认证明确后移到 P2，不把测试通过误记为生产认证验收。

## Migration Plan

无数据或配置迁移。修改解析器的无角色分支并添加测试、操作说明；如需回退，只恢复原解析行为与文档，不影响数据库或运行配置。

## Open Questions

无阻塞问题。真实 issuer、claims、人员编号和 break-glass 的设计与验收继续待 P2 确认。
