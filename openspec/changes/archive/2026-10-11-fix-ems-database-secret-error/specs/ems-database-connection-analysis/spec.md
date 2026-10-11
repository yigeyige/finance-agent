# EMS Database Connection Analysis Specification

## MODIFIED Requirements

### Requirement: Managed EMS database configuration

系统 SHALL 允许具有 `PLATFORM_ADMIN` 角色的身份维护 EMS MySQL 数据库连接配置，并且最多启用一条配置。密码 MUST 使用应用密钥加密保存，任何响应、日志和模型上下文 MUST NOT 返回密码或密文。未配置或长度不足的加密主密钥时，系统 MUST 拒绝保存并返回安全、可操作的配置不可用错误，MUST NOT 将其映射为业务状态冲突。

#### Scenario: Administrator tests a connection

- **WHEN** 平台管理员对有效配置执行连接测试
- **THEN** 系统以只读方式执行固定连通性检查并仅返回成功或安全失败结果

#### Scenario: Missing encryption master key

- **WHEN** 管理员保存连接且部署未配置有效的加密主密钥
- **THEN** 系统以安全的配置不可用错误拒绝请求，不返回密码、连接串或底层异常
