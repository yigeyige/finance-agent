## Why

现有注释位置和覆盖范围过于机械，降低注解可读性并制造无效文档噪音。

## What Changes

- 将 Javadoc 统一置于关联注解之前。
- 仅为具有业务语义或复杂行为的方法保留模板注释。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `backend-source-documentation`: 调整注释位置和方法注释适用范围。

## Impact

后端编码规范与生产源码注释；不改变运行行为。
