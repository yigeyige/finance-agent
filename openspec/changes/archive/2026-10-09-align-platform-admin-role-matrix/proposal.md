## Why

现有角色实现已将 PLATFORM_ADMIN 作为工作台最高权限角色，但旧规格仍描述其不自动获得源范围权限，造成设计与运行行为冲突。

## What Changes

- 将固定角色矩阵改为明确的平台管理员最高权限模型。
- 保留字段策略、来源适配器等非角色数据边界。

## Capabilities

### Modified Capabilities

- `role-permission-guard`: 对齐平台管理员最高权限与独立数据安全边界。

## Impact

- 权限规格与任务记录；不改动已生效的角色实现或数据库。
