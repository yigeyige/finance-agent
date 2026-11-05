## ADDED Requirements

### Requirement: Administrator manages platform users
系统 SHALL 仅允许 PLATFORM_ADMIN 新增、编辑、停用、启用用户以及设置和重置密码。新增必填登录账号、工号、显示名称、初始密码和角色；编辑不得改变登录账号和工号，响应 MUST NOT 返回密码或散列。

#### Scenario: Create account
- **WHEN** 管理员以唯一账号、工号和有效初始密码新增用户
- **THEN** 系统保存活动用户、指定角色和密码凭据，用户可登录

#### Scenario: Nonadministrator attempts management
- **WHEN** PLATFORM_USER 请求用户管理读或写接口
- **THEN** 系统拒绝请求

### Requirement: Protected administrator and disable semantics
系统 SHALL 将删除实现为停用，保留审计和业务记录；固定账号 `admin` 不得通过用户管理修改。管理员不得停用或降权自身，且系统 MUST 保留至少一位有效管理员。停用或角色变更 SHALL 对已有会话立即生效。

#### Scenario: Disable user
- **WHEN** 管理员停用其他普通用户
- **THEN** 该用户当前会话和后续登录均失效，既有任务及场景保留

#### Scenario: Protect administrator
- **WHEN** 管理员试图修改固定 admin 账号、停用自身或移除最后一名有效管理员
- **THEN** 系统拒绝操作且不修改用户、角色或密码
