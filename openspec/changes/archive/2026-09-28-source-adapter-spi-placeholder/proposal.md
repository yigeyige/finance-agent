## Why

当前配置源接口带有合成场景和 seed 参数，真实只读源无法按同一契约接入，也没有明确的生产未配置失败语义。需要先固化适配器 SPI 和 TCK，再把真实 API/只读视图留作阻断占位，避免伪成功或回退合成数据。

## What Changes

- 增加 `ApprovedQuery`、`ScopePage`、`RecheckResult` 等稳定只读契约，并保留现有合成接口兼容性。
- 为生产 profile 增加 API/只读视图适配器占位；无真实 `SourceCatalog` 时返回明确错误码，不返回合成数据。
- 增加适配器契约测试，验证空实现、错误码和合成实现的一致边界。

## Capabilities

### New Capabilities

- `source-adapter-spi`: 统一只读源 SPI、契约模型和生产未配置占位。

### Modified Capabilities

<!-- No existing requirement changes. -->

## Impact

- 影响 source/application 与 source/infrastructure 代码及测试。
- 不新增依赖、不实现真实字段映射、不修改数据库结构。
