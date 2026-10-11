# model-connection-management Specification

## Purpose

规定平台管理员维护大模型连接及其写入型 API Key 的安全边界，并保证配置管理页面仅呈现非敏感运行信息和必要操作。

## Requirements

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

### Requirement: Versioned model API key encryption format
系统 SHALL 对完整部署模型主密钥执行 SHA-256 派生 32 字节 AES-256 Key，并使用 AES-GCM 写入带 `v2:` 版本前缀的模型 API Key 密文；实现 MUST 使用随机 nonce 且不得记录主密钥、明文或密文。

#### Scenario: New API Key is saved with a multibyte master key
- **WHEN** 管理员在合规的多字节部署主密钥下保存模型 API Key
- **THEN** 系统成功写入带 `v2:` 前缀的认证密文，且可用同一主密钥解密

#### Scenario: Existing legacy ciphertext is read
- **WHEN** 数据库包含由旧截断规则写入的无前缀模型 API Key 密文
- **THEN** 系统使用兼容解密路径读取明文，但后续新写入不再产生旧格式

#### Scenario: Ciphertext authentication fails
- **WHEN** 模型 API Key 密文被篡改、格式损坏或主密钥不匹配
- **THEN** 系统以不含主密钥、明文或密文的安全错误失败

### Requirement: Unified model configuration entry layout
系统 SHALL 在大模型配置管理页面使用与其他管理入口一致的标题、操作区和圆角记录卡片。页面 MUST 在标题后直接展示配置操作和记录内容，不得展示重复的配置数量或启用状态总览卡片；非敏感记录信息仍在表格中展示。

#### Scenario: Administrator manages model configurations
- **WHEN** PLATFORM_ADMIN 进入大模型配置页面
- **THEN** 系统以统一入口布局展示模型配置记录，并保留新增、编辑、启用和停用操作及既有密钥保护行为
