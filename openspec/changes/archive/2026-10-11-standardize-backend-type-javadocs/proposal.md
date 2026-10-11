## Why

后端生产源码同时存在完整模板和旧式单行 Javadoc。近期新增的 EMS 数据库连接配置等类型未包含项目要求的版本、作者和日期元数据，降低了源码注释的一致性与可追溯性。

## What Changes

- 盘点 `backend/src/main/java` 中非包描述顶层 Java 类型的 Javadoc。
- 保留现有中文职责说明，将旧式或缺失元数据的类型注释改为统一模板。
- 对缺少类型注释的生产类型补充与其当前职责一致的中文说明。
- 不改变任何运行时逻辑、公开契约、配置或依赖。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `backend-source-documentation`: 明确类型 Javadoc 必须采用统一元数据模板。

## Impact

- 影响 `backend/src/main/java` 中类型注释及 `backend-source-documentation` 规格。
- 不影响 API、数据库、配置、依赖和运行时行为。
