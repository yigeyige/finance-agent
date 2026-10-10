# password-login-session Specification

## Purpose

为内网财务运维工作台提供最小、可退出的账号密码认证、会话身份解析和默认管理员初始化能力，并保持既有角色授权边界。

## Requirements

### Requirement: Password login establishes a trusted session

系统 SHALL 在身份模式为 PASSWORD 时接受受控长度的用户名和密码，并仅为活动用户且密码验证成功的请求建立随机不透明的服务端会话。会话 Cookie MUST 为 HttpOnly、Path `/api`、SameSite=Lax；系统 MUST NOT 在响应、日志或错误消息中返回密码、密码散列、盐或会话令牌。

#### Scenario: Administrator logs in successfully

- **WHEN** `admin` 使用正确的初始密码提交登录请求
- **THEN** 系统设置受保护的会话 Cookie，返回当前身份及 PLATFORM_ADMIN 角色，并允许后续工作台请求按该身份授权

#### Scenario: Invalid credential is rejected safely

- **WHEN** 用户名不存在、用户被禁用或密码不正确
- **THEN** 系统返回统一的未认证结果，不建立会话且不暴露失败原因

### Requirement: Session logout and expiration fail closed

系统 SHALL 支持显式退出和过期会话失效。退出时 MUST 删除服务端会话并以同一 Cookie 属性清除浏览器 Cookie；缺少、无效或过期会话 MUST 不产生当前身份。

#### Scenario: User logs out

- **WHEN** 已登录用户提交退出请求
- **THEN** 系统清除会话，后续受保护接口返回未认证结果

#### Scenario: Expired session is reused

- **WHEN** 请求携带已过期或未知的会话 Cookie
- **THEN** 系统不解析可信身份并拒绝受保护请求

### Requirement: Default platform administrator is initialized securely

系统 SHALL 通过前向数据库迁移创建 IAM 密码凭据表及中文注释，并幂等插入认证主体为 `admin` 的活动平台管理员、未撤销的 PLATFORM_ADMIN 角色和 PBKDF2-HMAC-SHA256 密码凭据。迁移 MUST 只包含散列、盐、迭代次数和版本信息，不得包含明文密码，且不得覆盖既有凭据。

#### Scenario: Fresh database applies login migration

- **WHEN** 空业务数据库执行登录相关 Flyway 迁移
- **THEN** 数据库拥有可登录的 `admin` 平台管理员和对应密码凭据，用户、角色和凭据均带中文表字段注释

#### Scenario: Existing administrator database is upgraded

- **WHEN** 已存在 `admin` 用户或密码凭据的数据库执行该迁移
- **THEN** 迁移保持已有记录和密码凭据，不重置管理员密码
