## Why

EMS 业务库可存在未维护表或字段注释的合法元数据。数据探索前端将空注释误判为接口格式错误，导致管理员无法浏览整个目录，需要立即修复该兼容性缺陷。

## What Changes

- 将数据探索表与字段元数据中的注释字段调整为“必须是字符串，但允许空字符串”。
- 增加解析边界验证，继续拒绝缺失、`null` 和非字符串注释。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `data-exploration`: 允许受控元数据响应中的表、字段注释为空文本。

## Impact

- 影响 `frontend/src/api/generated/index.ts` 的元数据解析与其前端测试；不改变后端、数据库、权限或依赖。
