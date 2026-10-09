## Why

当前真实模型连接信息依赖 YAML 与环境变量，管理员无法在工作台集中维护、切换或审计模型；业务场景 Skill 也尚未进入模型分析，难以让初稿遵循明确的业务场景。

## What Changes

- 新增管理员可维护的大模型配置：API 地址、API Key、模型名称、显示名称、推理强度和启用状态。
- API Key 仅在新增或更新时接收并安全持久化；查询、日志、模型元数据和前端均不返回明文。
- 模型调用在运行时从数据库读取唯一启用的大模型配置，不再使用 YAML 或环境变量中的模型连接信息。
- 业务处理初稿与聊天修订按任务标题、说明、处理方式和业务场景匹配当前有效的业务场景 Skill，并只将 Skill 的受控业务字段加入模型上下文。
- 左侧导航新增“大模型配置”页面，供具备平台配置权限的管理员维护连接信息。

## Capabilities

### New Capabilities

- `model-connection-management`: 管理员维护、启停和选择当前启用大模型连接，且密钥始终不可读。

### Modified Capabilities

- `deepseek-adapter`: 真实模型连接改为从受控持久化配置构造，不再读取 YAML 或环境变量的模型连接字段。
- `business-model-interaction`: 业务处理初稿和修订调用使用场景 Skill 受控上下文，并在没有启用模型配置时安全失败。
- `business-handling-guidance`: 场景匹配的 Skill 成为业务处理向导模型初稿的可追溯依据。
- `frontend-identity-navigation`: 平台配置权限用户可在左侧导航进入大模型配置页。

## Impact

- 新增模型配置表、迁移、领域对象、MyBatis-Plus Repository、服务、REST 接口、权限守卫与测试。
- DeepSeek 适配器、模型 Client 装配、模型调用上下文和健康诊断改为查询当前启用配置。
- 新增模型配置前端路由、服务和维护页面；侧栏将“配置管理”明确为“大模型配置”。
