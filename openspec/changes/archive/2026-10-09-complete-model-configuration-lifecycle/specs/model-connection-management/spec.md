## MODIFIED Requirements

### Requirement: Platform administrator manages model connections
系统 SHALL 仅允许 PLATFORM_ADMIN 创建、查看、编辑、启用和停用 OpenAI 兼容的大模型连接配置。每条配置 MUST 包含名称、显示名称、API URL、模型名称、推理强度、启用状态和审计字段；同一时刻最多一条配置处于启用状态。

#### Scenario: Administrator enables a model connection
- **WHEN** PLATFORM_ADMIN 启用一条具有有效 API URL、模型名称和已配置 Key 的连接
- **THEN** 系统在同一事务中停用原启用连接、启用目标连接并返回不含密钥的配置视图

#### Scenario: Administrator disables a model connection
- **WHEN** PLATFORM_ADMIN 停用一条存在的模型连接
- **THEN** 系统保留连接和密钥密文、将该连接标记为未启用、更新审计字段，且后续运行时选择不会返回该连接

#### Scenario: No connection remains enabled
- **WHEN** 管理员停用当前唯一启用连接后发起真实模型调用
- **THEN** 系统复用既有安全失败提示，要求管理员先启用一个模型且不产生伪造模型结果

#### Scenario: Unauthorized user manages model connections
- **WHEN** 非 PLATFORM_ADMIN 身份请求读取或修改模型配置
- **THEN** 系统拒绝请求且不返回或修改任何配置数据
