# model-connection-management Specification

## Purpose

规定平台管理员维护大模型连接及其写入型 API Key 的安全边界。

## Requirements

### Requirement: Platform administrator manages model connections
系统 SHALL 仅允许 PLATFORM_ADMIN 创建、查看、编辑、启用和停用 OpenAI 兼容的大模型连接配置。每条配置 MUST 包含名称、显示名称、API URL、模型名称、推理强度、启用状态和审计字段；同一时刻最多一条配置处于启用状态。

#### Scenario: Administrator enables a model connection
- **WHEN** PLATFORM_ADMIN 启用一条具有有效 API URL、模型名称和已配置 Key 的连接
- **THEN** 系统在同一事务中停用原启用连接、启用目标连接并返回不含密钥的配置视图

#### Scenario: Unauthorized user manages model connections
- **WHEN** 非 PLATFORM_ADMIN 身份请求读取或修改模型配置
- **THEN** 系统拒绝请求且不返回或修改任何配置数据

### Requirement: Model API key is write-only and encrypted
系统 SHALL 使用部署级加密主密钥以认证加密方式保存 API Key。模型配置读取响应、日志、审计、错误响应和调用元数据 MUST NOT 包含 API Key 明文、密文或可逆片段。

#### Scenario: Administrator reads configured connections
- **WHEN** PLATFORM_ADMIN 查询模型配置列表或详情
- **THEN** 系统仅返回 `apiKeyConfigured` 状态，不返回 API Key 的任何内容

#### Scenario: Administrator preserves existing API key
- **WHEN** PLATFORM_ADMIN 编辑连接且未提交新的 API Key
- **THEN** 系统保留已有密文，不要求前端回传或展示原 API Key

#### Scenario: Encryption master key is unavailable
- **WHEN** 系统未配置部署级加密主密钥且管理员尝试保存或替换 API Key
- **THEN** 系统拒绝写入并返回安全的配置错误，不写入明文 Key
